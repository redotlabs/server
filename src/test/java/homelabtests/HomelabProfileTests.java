package homelabtests;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.assertThat;

class HomelabProfileTests {
    @Test void runtimeOverridesResolveWithoutAnEmbeddedProductionProfile() throws Exception {
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new MapPropertySource("synthetic-runtime", Map.of(
                "DB_HOST", "db.example.invalid", "DB_PORT", "15432", "DB_NAME", "contract",
                "DB_USERNAME", "runtime-role", "AWS_REGION", "ap-northeast-2",
                "JWT_COOKIE_DOMAIN", "api.example.invalid", "JWT_COOKIE_SAME_SITE", "Strict")));
        var sources = new YamlPropertySourceLoader().load("homelab", new ClassPathResource("application-homelab.yml"));
        sources.forEach(source -> env.getPropertySources().addLast(source));
        assertThat(env.getProperty("spring.datasource.url")).isEqualTo("jdbc:postgresql://db.example.invalid:15432/contract");
        assertThat(env.getProperty("spring.datasource.username")).isEqualTo("runtime-role");
        assertThat(env.getProperty("cloud.aws.region.static")).isEqualTo("ap-northeast-2");
        assertThat(env.getProperty("jwt.cookie.domain")).isEqualTo("api.example.invalid");
        assertThat(env.getProperty("jwt.cookie.same-site")).isEqualTo("Strict");
        assertThat(env.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
        assertThat(env.getProperty("spring.flyway.enabled")).isEqualTo("true");
        assertThat(env.getProperty("management.endpoint.health.group.liveness.include")).isEqualTo("livenessState");
        assertThat(env.getProperty("management.endpoint.health.group.readiness.include")).isEqualTo("readinessState,db,redis");
        assertThat(env.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
    }
}
