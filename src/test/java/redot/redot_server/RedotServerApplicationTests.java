package redot.redot_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redot.redot_server.global.jwt.cookie.CookieProvider;
import redot.redot_server.global.jwt.cookie.CookieProviderDev;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(RedotServerApplicationTests.TestCookies.class)
class RedotServerApplicationTests {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @TestConfiguration
    static class TestCookies {
        @Bean
        CookieProvider cookieProvider() {
            return new CookieProviderDev(false, "Lax");
        }
    }

    @Test
    void contextLoads() {
    }

}
