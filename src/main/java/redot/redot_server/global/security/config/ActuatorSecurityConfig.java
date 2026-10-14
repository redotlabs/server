package redot.redot_server.global.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class ActuatorSecurityConfig {
    @Bean
    @Order(-100)
    SecurityFilterChain actuatorSecurityChain(HttpSecurity http,
            @Value("${redot.health.public-enabled:false}") boolean publicHealth) throws Exception {
        http.securityMatcher("/actuator", "/actuator/**");
        http.authorizeHttpRequests(auth -> {
            if (publicHealth) {
                auth.requestMatchers(HttpMethod.GET, "/actuator/health",
                        "/actuator/health/liveness", "/actuator/health/readiness").permitAll();
            }
            auth.anyRequest().denyAll();
        });
        return http.build();
    }
}
