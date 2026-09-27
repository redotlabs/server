package homelabtests;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import redot.redot_server.global.security.config.ActuatorSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HealthProbeTests.ProbeApplication.class, properties = {
        "redot.health.public-enabled=true", "management.endpoint.health.access=read-only",
        "management.endpoints.web.exposure.include=health,env",
        "management.endpoint.env.access=read-only", "management.endpoint.health.probes.enabled=true",
        "management.endpoint.health.show-details=never", "management.endpoint.health.show-components=never",
        "management.endpoint.health.group.liveness.include=livenessState",
        "management.endpoint.health.group.readiness.include=readinessState,db,redis"
})
@AutoConfigureMockMvc
class HealthProbeTests {
    @Autowired MockMvc mvc;
    @Autowired ApplicationContext context;
    @Autowired Dependencies dependencies;

    @BeforeEach void reset() {
        dependencies.db.set(true);
        dependencies.redis.set(true);
        AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
        AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
    }

    @Test void healthyProbesAreAnonymousAndDoNotRevealComponents() throws Exception {
        for (String path : new String[]{"/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness"}) {
            mvc.perform(get(path)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.components").doesNotExist())
                    .andExpect(jsonPath("$.details").doesNotExist());
        }
    }

    @Test void databaseFailureDoesNotFailLiveness() throws Exception {
        dependencies.db.set(false);
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isServiceUnavailable());
    }

    @Test void redisFailureDoesNotFailLiveness() throws Exception {
        dependencies.redis.set(false);
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isServiceUnavailable());
    }

    @Test void refusingTrafficAffectsReadinessOnly() throws Exception {
        AvailabilityChangeEvent.publish(context, ReadinessState.REFUSING_TRAFFIC);
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isServiceUnavailable());
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
    }

    @Test void brokenApplicationFailsLiveness() throws Exception {
        AvailabilityChangeEvent.publish(context, LivenessState.BROKEN);
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isServiceUnavailable());
    }

    @Test void sensitiveAndUnlistedEndpointsRemainDeniedEvenIfExposed() throws Exception {
        for (String path : new String[]{"/actuator/env", "/actuator/configprops", "/actuator/heapdump",
                "/actuator/health/db", "/actuator"}) {
            mvc.perform(get(path)).andExpect(status().isForbidden());
        }
        mvc.perform(post("/actuator/health")).andExpect(status().isForbidden());
    }

    @Test void healthChainDoesNotAuthorizeOtherApiPaths() throws Exception {
        mvc.perform(get("/api/protected")).andExpect(status().isForbidden());
    }

    static class Dependencies {
        final AtomicBoolean db = new AtomicBoolean(true);
        final AtomicBoolean redis = new AtomicBoolean(true);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class,
            UserDetailsServiceAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
    @Import(ActuatorSecurityConfig.class)
    static class ProbeApplication {
        @Bean Dependencies dependencies() { return new Dependencies(); }
        @Bean HealthIndicator dbHealthIndicator(Dependencies d) {
            return () -> d.db.get() ? Health.up().build() : Health.down().build();
        }
        @Bean HealthIndicator redisHealthIndicator(Dependencies d) {
            return () -> d.redis.get() ? Health.up().build() : Health.down().build();
        }
        @Bean @Order(0) SecurityFilterChain protectedApi(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(auth -> auth.anyRequest().denyAll()).build();
        }
    }
}
