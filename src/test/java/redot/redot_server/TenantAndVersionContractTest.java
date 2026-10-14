package redot.redot_server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redot.redot_server.global.redotapp.context.RedotAppContextHolder;
import redot.redot_server.support.RuntimePostgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real security chain, normal login cookies, Flyway schema and disposable PostgreSQL.
 * No test transaction: assertions observe committed writes and actual rollback boundaries.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("container")
@Testcontainers
@Import(RedotServerApplicationTests.TestCookies.class)
class TenantAndVersionContractTest {
    private static final String HEADER = "X-App-Subdomain";
    private static final String INQUIRIES = "/api/v1/app/cms/inquiries";
    private static final String VERSIONS = "/api/v1/app/cms/pages/versions";
    @Container static final PostgreSQLContainer<?> POSTGRES = RuntimePostgres.create();
    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;
    @Autowired PasswordEncoder encoder;
    Cookie a;
    Cookie b;

    @BeforeEach
    void fixtures() throws Exception {
        // This JDBC connection belongs exclusively to the disposable Testcontainer.
        jdbc.execute("TRUNCATE app_plans, inquiry_sequences RESTART IDENTITY CASCADE");
        jdbc.update("INSERT INTO app_plans(plan_type,display_name,price,max_page_views,max_pages,max_managers) VALUES('FREE','Test',0,100,10,2)");
        jdbc.update("INSERT INTO redot_apps(plan_id,status,name) VALUES(1,'ACTIVE','A'),(1,'ACTIVE','B')");
        jdbc.update("INSERT INTO domains(subdomain,redot_app_id,reserved) VALUES('tenant-a',1,false),('tenant-b',2,false)");
        for (long id : List.of(1L, 2L)) {
            jdbc.update("INSERT INTO cms_members(redot_app_id,name,email,password,role) VALUES(?,?,'owner@example.invalid',?,'OWNER')",
                    id, "Owner " + id, encoder.encode("fixture-password"));
        }
        a = login("tenant-a");
        b = login("tenant-b");
    }

    @Test
    void inquiryListsAndDetailsStayWithinEachTenant() throws Exception {
        long aid = inquiry("tenant-a", a, "A only");
        long bid = inquiry("tenant-b", b, "B only");
        auth(get(INQUIRIES), "tenant-a", a).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(aid));
        auth(get(INQUIRIES), "tenant-b", b).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(bid));
        auth(get(INQUIRIES + "/" + aid), "tenant-a", a).andExpect(status().isOk());
        auth(get(INQUIRIES + "/" + bid), "tenant-a", a).andExpect(status().isNotFound());
        auth(get(INQUIRIES + "/" + aid), "tenant-b", b).andExpect(status().isNotFound());
    }

    @Test
    void mismatchedTokenCannotReadOrWriteAndContextIsCleared() throws Exception {
        auth(get(INQUIRIES), "tenant-b", a).andExpect(status().isUnauthorized());
        assertThat(RedotAppContextHolder.get()).isNull();
        auth(post(INQUIRIES).contentType("application/json").content(inquiryBody("blocked")), "tenant-b", a)
                .andExpect(status().isUnauthorized());
        assertThat(count("redot_app_inquiries")).isZero();
        mvc.perform(get(INQUIRIES).cookie(a)).andExpect(status().isUnauthorized());
        assertThat(RedotAppContextHolder.get()).isNull();
        auth(get(INQUIRIES), "tenant-a", a).andExpect(status().isOk());
        assertThat(RedotAppContextHolder.get()).isNull();
    }

    @Test
    void foreignInquiryCannotBeCompletedOrReopened() throws Exception {
        long id = inquiry("tenant-b", b, "B only");
        auth(patch(INQUIRIES + "/" + id + "/complete"), "tenant-a", a).andExpect(status().isNotFound());
        assertThat(inquiryState(id)).isEqualTo("UNPROCESSED");
        assertThat(jdbc.queryForObject("SELECT assignee_id FROM redot_app_inquiries WHERE id=?", Long.class, id)).isNull();
        auth(patch(INQUIRIES + "/" + id + "/complete"), "tenant-b", b).andExpect(status().isOk());
        auth(patch(INQUIRIES + "/" + id + "/reopen"), "tenant-a", a).andExpect(status().isNotFound());
        assertThat(inquiryState(id)).isEqualTo("COMPLETED");
        assertThat(jdbc.queryForObject("SELECT assignee_id FROM redot_app_inquiries WHERE id=?", Long.class, id)).isEqualTo(2L);
        auth(patch(INQUIRIES + "/" + id + "/reopen"), "tenant-b", b).andExpect(status().isOk());
        assertThat(inquiryState(id)).isEqualTo("UNPROCESSED");
    }

    @Test
    void foreignPageCannotBeReadOrRetainedAndFailedPublishLeavesNoRows() throws Exception {
        long bv = version("tenant-b", b, "PUBLISHED", List.of(), List.of(page("/", "B")));
        long bp = pageId(bv, "/");
        long av = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "A")));
        auth(get("/api/v1/app/cms/pages/" + bp), "tenant-a", a).andExpect(status().isForbidden());
        auth(get("/api/v1/app/cms/pages/" + bp), "tenant-b", b).andExpect(status().isOk());
        createVersion("tenant-a", a, "PUBLISHED", List.of(Map.of("id", bp)), List.of())
                .andExpect(status().isForbidden());
        assertThat(count("app_versions")).isEqualTo(2);
        assertThat(count("app_pages")).isEqualTo(2);
        assertThat(count("app_version_pages")).isEqualTo(2);
        assertThat(versionState(av)).isEqualTo("PUBLISHED");
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("A"));
        assertThat(RedotAppContextHolder.get()).isNull();
    }

    @Test
    void versionListAndPublicPageAreTenantScopedEvenForSamePath() throws Exception {
        long av = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "A")));
        long bv = version("tenant-b", b, "PUBLISHED", List.of(), List.of(page("/", "B")));
        auth(get(VERSIONS), "tenant-a", a).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(av));
        auth(get(VERSIONS), "tenant-b", b).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(bv));
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("A"));
        publicPage("tenant-b", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("B"));
    }

    @Test
    void draftWithoutPublishedVersionIsNotPublic() throws Exception {
        long draft = version("tenant-a", a, "DRAFT", List.of(), List.of(page("/", "draft")));
        publicPage("tenant-a", "/").andExpect(status().isNotFound());
        auth(get("/api/v1/app/cms/pages/" + pageId(draft, "/")), "tenant-a", a)
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").value("draft"));
    }

    @Test
    void savingDraftDoesNotReplacePublishedContent() throws Exception {
        long current = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "live")));
        version("tenant-a", a, "DRAFT", List.of(), List.of(page("/", "draft"), page("/new", "hidden")));
        assertThat(versionState(current)).isEqualTo("PUBLISHED");
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("live"));
        publicPage("tenant-a", "/new").andExpect(status().isNotFound());
    }

    @Test
    void publishingSwitchesWholeSnapshotAndPreservesPreviousContent() throws Exception {
        long old = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "old"), page("/removed", "old-only")));
        long oldPage = pageId(old, "/");
        long next = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "new")));
        assertThat(versionState(old)).isEqualTo("PREVIOUS");
        assertThat(versionState(next)).isEqualTo("PUBLISHED");
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("new"));
        publicPage("tenant-a", "/removed").andExpect(status().isNotFound());
        auth(get("/api/v1/app/cms/pages/" + oldPage), "tenant-a", a)
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").value("old"));
    }

    @Test
    void unchangedPagesAreSharedAcrossSnapshotsWithoutCopyingContentRows() throws Exception {
        long old = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "old"), page("/about", "same")));
        long shared = pageId(old, "/about");
        long next = version("tenant-a", a, "PUBLISHED", List.of(Map.of("id", shared)), List.of(page("/", "new")));
        assertThat(pageId(next, "/about")).isEqualTo(shared);
        assertThat(count("app_pages")).isEqualTo(3);
        assertThat(count("app_version_pages")).isEqualTo(4);
        publicPage("tenant-a", "/about").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("same"));
        assertThat(jdbc.queryForObject("SELECT content FROM app_pages WHERE id=?", String.class, pageId(old, "/"))).isEqualTo("old");
    }

    @Test
    void duplicatePathFailureRollsBackStatusAndAlreadyInsertedPage() throws Exception {
        long old = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "live")));
        createVersion("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "first"), page("/", "duplicate")))
                .andExpect(status().isBadRequest());
        assertThat(versionState(old)).isEqualTo("PUBLISHED");
        assertThat(count("app_versions")).isEqualTo(1);
        assertThat(count("app_pages")).isEqualTo(1);
        assertThat(count("app_version_pages")).isEqualTo(1);
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("live"));
    }

    @Test
    void retainedAndAddedPathCollisionAlsoRollsBack() throws Exception {
        long old = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "live")));
        createVersion("tenant-a", a, "PUBLISHED", List.of(Map.of("id", pageId(old, "/"))), List.of(page("/", "collision")))
                .andExpect(status().isBadRequest());
        assertThat(count("app_versions")).isEqualTo(1);
        assertThat(count("app_pages")).isEqualTo(1);
        assertThat(versionState(old)).isEqualTo("PUBLISHED");
    }

    @Test
    void missingRetainedPageDoesNotWithdrawCurrentPublication() throws Exception {
        long old = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "live")));
        createVersion("tenant-a", a, "PUBLISHED", List.of(Map.of("id", 999999L)), List.of())
                .andExpect(status().isNotFound());
        assertThat(count("app_versions")).isEqualTo(1);
        assertThat(versionState(old)).isEqualTo("PUBLISHED");
    }

    @Test
    void previousCannotBeCreatedDirectlyButMultipleHistoricalVersionsRemain() throws Exception {
        createVersion("tenant-a", a, "PREVIOUS", List.of(), List.of()).andExpect(status().isBadRequest());
        assertThat(count("app_versions")).isZero();
        long first = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "1")));
        long second = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "2")));
        long third = version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "3")));
        assertThat(versionState(first)).isEqualTo("PREVIOUS");
        assertThat(versionState(second)).isEqualTo("PREVIOUS");
        assertThat(versionState(third)).isEqualTo("PUBLISHED");
    }

    @Test
    void flywayConstraintRejectsSecondPublishedVersionForSameTenant() throws Exception {
        version("tenant-a", a, "PUBLISHED", List.of(), List.of(page("/", "live")));
        assertThatThrownBy(() -> jdbc.update("INSERT INTO app_versions(redot_app_id,status) VALUES(1,'PUBLISHED')"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("uk_app_versions_published_per_app");
        version("tenant-b", b, "PUBLISHED", List.of(), List.of(page("/", "B")));
        assertThat(count("app_versions")).isEqualTo(2);
    }

    @Test
    void publishingDraftPagesCreatesNewVersionRatherThanPromotingExistingDraft() throws Exception {
        long draft = version("tenant-a", a, "DRAFT", List.of(), List.of(page("/", "ready")));
        long page = pageId(draft, "/");
        long published = version("tenant-a", a, "PUBLISHED", List.of(Map.of("id", page)), List.of());
        assertThat(published).isNotEqualTo(draft);
        assertThat(versionState(draft)).isEqualTo("DRAFT");
        assertThat(versionState(published)).isEqualTo("PUBLISHED");
        assertThat(pageId(published, "/")).isEqualTo(page);
        assertThat(count("app_pages")).isEqualTo(1);
        publicPage("tenant-a", "/").andExpect(status().isOk()).andExpect(jsonPath("$.content").value("ready"));
    }

    @Test
    void flywayConstraintRejectsTwoDifferentPagesAtSamePathWithinVersion() throws Exception {
        long first = version("tenant-a", a, "DRAFT", List.of(), List.of(page("/", "first")));
        long second = version("tenant-a", a, "DRAFT", List.of(), List.of(page("/", "second")));
        long secondPage = pageId(second, "/");
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO app_version_pages(app_version_id,app_page_id,path) VALUES(?,?, '/')", first, secondPage))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("uk_app_version_page_path");
        assertThat(count("app_version_pages")).isEqualTo(2);
    }

    private Cookie login(String tenant) throws Exception {
        var response = mvc.perform(post("/api/v1/auth/app/cms/sign-in").header(HEADER, tenant)
                        .contentType("application/json").content(json.writeValueAsString(Map.of("email", "owner@example.invalid", "password", "fixture-password"))))
                .andExpect(status().isOk()).andReturn().getResponse();
        Cookie cookie = response.getCookie("redot_cms_access_token");
        assertThat(cookie).isNotNull();
        return cookie;
    }
    private ResultActions auth(MockHttpServletRequestBuilder request, String tenant, Cookie cookie) throws Exception {
        return mvc.perform(request.header(HEADER, tenant).cookie(cookie));
    }
    private String inquiryBody(String title) throws Exception {
        return json.writeValueAsString(Map.of("inquirerName", "fixture", "title", title, "content", title));
    }
    private long inquiry(String tenant, Cookie cookie, String title) throws Exception {
        return body(auth(post(INQUIRIES).contentType("application/json").content(inquiryBody(title)), tenant, cookie)
                .andExpect(status().isOk())).get("id").asLong();
    }
    private Map<String, Object> page(String path, String content) {
        return Map.of("path", path, "content", content, "title", content, "isProtected", false);
    }
    private ResultActions createVersion(String tenant, Cookie cookie, String state, List<?> retained, List<?> added) throws Exception {
        return auth(post(VERSIONS).contentType("application/json").content(json.writeValueAsString(
                Map.of("status", state, "retained", retained, "added", added))), tenant, cookie);
    }
    private long version(String tenant, Cookie cookie, String state, List<?> retained, List<?> added) throws Exception {
        return body(createVersion(tenant, cookie, state, retained, added).andExpect(status().isOk())).get("id").asLong();
    }
    private ResultActions publicPage(String tenant, String path) throws Exception {
        return mvc.perform(get("/api/v1/app/site/pages").header(HEADER, tenant).param("path", path));
    }
    private JsonNode body(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsByteArray());
    }
    private long pageId(long version, String path) {
        return jdbc.queryForObject("SELECT app_page_id FROM app_version_pages WHERE app_version_id=? AND path=?", Long.class, version, path);
    }
    private String versionState(long id) {
        return jdbc.queryForObject("SELECT status FROM app_versions WHERE id=?", String.class, id);
    }
    private String inquiryState(long id) {
        return jdbc.queryForObject("SELECT status FROM redot_app_inquiries WHERE id=?", String.class, id);
    }
    private long count(String table) {
        return jdbc.queryForObject("SELECT count(*) FROM " + table, Long.class);
    }
}
