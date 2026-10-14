package redot.redot_server.domain.cms.inquiry.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import redot.redot_server.support.RuntimePostgres;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.atomic.AtomicInteger;
import redot.redot_server.domain.cms.inquiry.repository.InquirySequenceRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryException;
import redot.redot_server.global.config.QueryDSLConfig;

@DataJpaTest(showSql = false, properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.show-sql=false",
        "spring.datasource.hikari.maximum-pool-size=16",
        "spring.datasource.hikari.connection-timeout=10000"
})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDSLConfig.class, InquiryNumberGenerator.class, RedotAppInquiryService.class,
        InquiryConcurrencyTest.TimeConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Tag("container")
@Testcontainers
class InquiryConcurrencyTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = RuntimePostgres.create();

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired RedotAppInquiryService service;
    @Autowired TestClock clock;
    @Autowired InquirySequenceRepository sequences;
    @Autowired PlatformTransactionManager transactionManager;

    // Removing @Lock makes the second transaction finish before the first commits.
    @Test
    void existingSequenceLockBlocksSecondTransactionUntilCommit() throws Exception {
        jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, 40)",
                LocalDate.now(clock));
        var tx = new TransactionTemplate(transactionManager);
        var locked = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var secondPid = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var first = executor.submit(() -> tx.executeWithoutResult(status -> {
                var row = sequences.findByInquiryDateForUpdate(LocalDate.now(clock)).orElseThrow();
                row.updateLastSequenceNumber(row.getLastSequenceNumber() + 1);
                locked.countDown();
                try {
                    if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Release timed out");
                } catch (InterruptedException ex) { throw new RuntimeException(ex); }
            }));
            Future<?> second = null;
            try {
                assertThat(locked.await(10, TimeUnit.SECONDS)).isTrue();
                second = executor.submit(() -> tx.executeWithoutResult(status -> {
                    secondPid.set(jdbc.queryForObject("SELECT pg_backend_pid()", Integer.class));
                    var row = sequences.findByInquiryDateForUpdate(LocalDate.now(clock)).orElseThrow();
                    row.updateLastSequenceNumber(row.getLastSequenceNumber() + 1);
                }));
                boolean blocked = false;
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                while (System.nanoTime() < deadline && !second.isDone()) {
                    blocked = Boolean.TRUE.equals(jdbc.queryForObject(
                            "SELECT cardinality(pg_blocking_pids(?)) > 0", Boolean.class, secondPid.get()));
                    if (blocked) break;
                    Thread.sleep(10);
                }
                assertThat(blocked).as("Second transaction must wait on the existing date row lock").isTrue();
            } finally { release.countDown(); }
            first.get(10, TimeUnit.SECONDS);
            if (second != null) second.get(10, TimeUnit.SECONDS);
        }
        assertThat(jdbc.queryForObject("SELECT last_sequence_number FROM inquiry_sequences", Long.class))
                .isEqualTo(42L);
    }

    @TestConfiguration
    static class TimeConfiguration {
        @Bean
        TestClock inquiryClock() { return new TestClock(); }
    }

    static class TestClock extends Clock {
        private volatile Instant now = Instant.parse("2026-10-13T14:59:59Z");
        @Override public ZoneId getZone() { return ZoneId.of("Asia/Seoul"); }
        @Override public Clock withZone(ZoneId zone) { return Clock.fixed(now, zone); }
        @Override public Instant instant() { return now; }
    }

    @BeforeEach
    void createTenants() {
        clock.now = Instant.parse("2026-10-13T14:59:59Z");
        jdbc.execute("TRUNCATE TABLE app_plans, inquiry_sequences RESTART IDENTITY CASCADE");
        jdbc.update("""
                INSERT INTO app_plans (plan_type, display_name, price, max_page_views, max_pages, max_managers)
                VALUES ('FREE', 'Test', 0, 100, 10, 2)
                """);
        jdbc.update("INSERT INTO redot_apps (plan_id, status, name) VALUES (1, 'ACTIVE', 'A'), (1, 'ACTIVE', 'B')");
    }

    // Detects losing valid requests when several transactions insert the first date row.
    @ParameterizedTest
    @ValueSource(ints = {2, 10, 100})
    void allFirstInquiriesCommitWithUniqueNumbers(int requests) throws Exception {
        assertConcurrentInquiries(requests, 0);
    }

    // Detects lost increments if serialization of an existing date row is removed.
    @ParameterizedTest
    @ValueSource(ints = {2, 10, 100})
    void existingDateSerializesInquiriesAcrossTenants(int requests) throws Exception {
        jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, 40)",
                LocalDate.now(clock));
        assertConcurrentInquiries(requests, 40);
    }

    @Test
    void midnightInSeoulStartsANewDateSequence() {
        assertThat(createInquiry("Content")).isEqualTo("INQ2026101300001");
        clock.now = Instant.parse("2026-10-13T15:00:00Z");
        assertThat(createInquiry("Content")).isEqualTo("INQ2026101400001");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM inquiry_sequences", Long.class)).isEqualTo(2L);
    }

    @Test
    void lastAvailableNumberSucceedsAndOverflowDoesNotPersist() {
        jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, 99998)",
                LocalDate.now(clock));
        assertThat(createInquiry("Content")).isEqualTo("INQ2026101399999");
        assertThatThrownBy(() -> createInquiry("Content")).isInstanceOf(RedotAppInquiryException.class);
        assertThat(jdbc.queryForObject("SELECT last_sequence_number FROM inquiry_sequences", Long.class))
                .isEqualTo(99999L);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries", Long.class)).isEqualTo(1L);
    }

    @Test
    void competingRequestsForLastNumberCommitExactlyOneInquiry() throws Exception {
        jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, 99998)",
                LocalDate.now(clock));
        var start = new CountDownLatch(1);
        var ready = new CountDownLatch(10);
        List<Future<Boolean>> attempts = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) attempts.add(executor.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Start timed out");
                try {
                    assertThat(createInquiry("Content")).isEqualTo("INQ2026101399999");
                    return true;
                } catch (RedotAppInquiryException exhausted) { return false; }
            }));
            try { assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue(); }
            finally { start.countDown(); }
            int successes = 0;
            for (var attempt : attempts) if (attempt.get(30, TimeUnit.SECONDS)) successes++;
            assertThat(successes).isEqualTo(1);
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries", Long.class)).isEqualTo(1L);
        assertThat(jdbc.queryForObject("SELECT last_sequence_number FROM inquiry_sequences", Long.class)).isEqualTo(99999L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 40})
    void failedInquirySaveRollsBackItsSequenceChange(int initial) {
        if (initial > 0) {
            jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, ?)",
                    LocalDate.now(clock), initial);
        }
        assertThatThrownBy(() -> createInquiry(null)).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries", Long.class)).isZero();
        if (initial == 0) {
            assertThat(jdbc.queryForObject("SELECT count(*) FROM inquiry_sequences", Long.class)).isZero();
        } else {
            assertThat(jdbc.queryForObject("SELECT last_sequence_number FROM inquiry_sequences", Long.class))
                    .isEqualTo((long) initial);
        }
        assertThat(createInquiry("Content")).isEqualTo("INQ20261013%05d".formatted(initial + 1));
    }

    private String createInquiry(String content) {
        return service.createInquiry(1L,
                new RedotAppInquiryCreateRequest("Tester", "Question", content)).inquiryNumber();
    }

    private void assertConcurrentInquiries(int requests, long startingSequence) throws Exception {
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        List<Throwable> failures = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < requests; i++) {
                long tenant = i % 2 + 1;
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(10, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Start barrier timed out");
                    }
                    return service.createInquiry(tenant,
                            new RedotAppInquiryCreateRequest("Tester", "Question", "Content")).inquiryNumber();
                }));
            }
            try {
                assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            } finally {
                start.countDown();
            }
            for (Future<String> future : futures) {
                try {
                    numbers.add(future.get(30, TimeUnit.SECONDS));
                } catch (java.util.concurrent.ExecutionException ex) {
                    failures.add(ex.getCause());
                }
            }
        }
        Long committed = jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries", Long.class);
        Long unique = jdbc.queryForObject("SELECT count(DISTINCT inquiry_number) FROM redot_app_inquiries", Long.class);
        System.out.printf("INQUIRY_RESULT requests=%d initial=%d succeeded=%d failed=%d committed=%d unique=%d%n",
                requests, startingSequence, numbers.size(), failures.size(), committed, unique);
        assertThat(failures).as("Every valid inquiry must commit; failures: %s", failures).isEmpty();
        assertThat(numbers).hasSize(requests).doesNotHaveDuplicates();
        assertThat(committed).isEqualTo((long) requests);
        assertThat(unique).isEqualTo((long) requests);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM inquiry_sequences", Long.class)).isEqualTo(1L);
        assertThat(jdbc.queryForObject("SELECT last_sequence_number FROM inquiry_sequences", Long.class))
                .isEqualTo(startingSequence + requests);
    }
}
