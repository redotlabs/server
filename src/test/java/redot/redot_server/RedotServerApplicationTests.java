package redot.redot_server;

import java.util.UUID;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import redot.redot_server.global.security.social.resolver.FlowAwareRedirectUriResolver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;

// This remains a full application context test, now with explicit isolated dependencies.
// Run using scripts/test-kubernetes.py; no production credentials are required.
@Tag("integration")
@ActiveProfiles("homelab")
@SpringBootTest
@AutoConfigureMockMvc
class RedotServerApplicationTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired FlowAwareRedirectUriResolver oauthResolver;
    @Autowired FilterChainProxy security;
    @Autowired MockMvc mvc;
    @Autowired StringRedisTemplate redis;
    private static final String GENERATED_TEST_VALUE = UUID.randomUUID().toString() + UUID.randomUUID();

    @DynamicPropertySource
    static void runtime(DynamicPropertyRegistry properties) {
        properties.add("DB_HOST", () -> requiredTestEnv("REDOT_IT_DB_HOST"));
        properties.add("DB_PORT", () -> "5432");
        properties.add("DB_NAME", () -> "redot_test");
        properties.add("DB_USERNAME", () -> "redot_test");
        properties.add("DB_PASSWORD", () -> requiredTestEnv("REDOT_IT_DB_PASSWORD"));
        properties.add("REDIS_HOST", () -> requiredTestEnv("REDOT_IT_REDIS_HOST"));
        properties.add("REDIS_PASSWORD", () -> requiredTestEnv("REDOT_IT_REDIS_PASSWORD"));
        for (String name : new String[]{"JWT_SECRET", "GOOGLE_CLIENT_ID", "GOOGLE_CLIENT_SECRET",
                "SMTP_USERNAME", "SMTP_PASSWORD", "AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"}) {
            properties.add(name, () -> GENERATED_TEST_VALUE);
        }
        properties.add("AWS_REGION", () -> "ap-northeast-2");
        properties.add("S3_BUCKET", () -> "redot-test-not-a-real-bucket");
        properties.add("SMTP_HOST", () -> "smtp.example.invalid");
        properties.add("MAIL_FROM", () -> "test@example.invalid");
        properties.add("GOOGLE_REDIRECT_URI", () -> "https://redot-api.dhxxn.kr/api/v1/sign-in/redot-member/social/callback/redot-member-google");
        properties.add("AUTH_SUCCESS_REDIRECT", () -> "https://frontend.example.invalid/success");
        properties.add("AUTH_FAILURE_REDIRECT", () -> "https://frontend.example.invalid/failure");
        properties.add("AUTH_ALLOWED_REDIRECT_HOSTS", () -> "frontend.example.invalid");
        properties.add("CORS_ALLOWED_ORIGINS", () -> "https://frontend.example.invalid");
    }

    private static String requiredTestEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Run scripts/test-kubernetes.py to provide isolated test dependencies: " + name);
        }
        return value;
    }

    @Test
    void contextLoads() {
        // Flyway has run and Hibernate ddl-auto=validate has completed before this method.
        assertThat(jdbc.queryForObject("SHOW server_version", String.class)).startsWith("17.11");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM flyway_schema_history WHERE success", Integer.class)).isEqualTo(7);
        assertThat(jdbc.queryForList("SELECT version FROM flyway_schema_history ORDER BY installed_rank", String.class))
                .containsExactly("1", "2", "3", "4", "5", "6", "7");
    }

    @Test
    void redisAcceptsExpiringVerificationState() {
        String key = "redot-integration:" + UUID.randomUUID();
        try {
            redis.opsForValue().set(key, GENERATED_TEST_VALUE, Duration.ofSeconds(60));
            assertThat(GENERATED_TEST_VALUE.equals(redis.opsForValue().get(key))).isTrue();
            assertThat(redis.getExpire(key)).isBetween(1L, 60L);
        } finally {
            redis.delete(key);
        }
    }
    private MockHttpServletRequest oauthRequest(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        request.setScheme("https");
        request.setServerName("redot-api.dhxxn.kr");
        request.setServerPort(443);
        request.setSecure(true);
        return request;
    }

    @Test
    void googleCallbackMatchesRegistrationAndSecurityChain() {
        String callbackPath = "/api/v1/sign-in/redot-member/social/callback/redot-member-google";
        var request = oauthRequest("/api/v1/oauth2/authorization/redot-member-google");
        var authorization = oauthResolver.resolve(request);
        assertThat(authorization).isNotNull();
        assertThat(authorization.getRedirectUri()).isEqualTo("https://redot-api.dhxxn.kr" + callbackPath);
        var callback = oauthRequest(callbackPath);
        var selected = security.getFilterChains().stream().filter(chain -> chain.matches(callback)).findFirst().orElseThrow();
        assertThat(selected.getFilters().stream().anyMatch(OAuth2LoginAuthenticationFilter.class::isInstance)).isTrue();
    }

    @Test
    void existingFrontendOverrideBehaviorIsExplicitAndUnchanged() {
        var request = oauthRequest("/api/v1/oauth2/authorization/redot-member-google");
        request.setParameter("redirect_uri", "https://frontend.example.invalid/success");
        assertThat(oauthResolver.resolve(request).getRedirectUri()).isEqualTo(
                "https://frontend.example.invalid/api/v1/sign-in/redot-member/social/callback/redot-member-google");
    }

    @Test
    void realContextHasReadyProbesButProtectedMemberApi() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/env")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/auth/redot/member/me")).andExpect(status().isUnauthorized());
    }

}
