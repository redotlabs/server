package redot.redot_server.global.security.config;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration(proxyBeanMethods = false)
@Profile("homelab")
public class HomelabCorsConfig {
    @Bean
    public FilterRegistrationBean<CorsFilter> homelabCorsFilter(
            @Value("${redot.cors.allowed-origins}") String configuredOrigins) {
        List<String> origins = Arrays.stream(configuredOrigins.split(","))
                .map(String::trim).toList();
        for (String origin : origins) {
            URI uri = URI.create(origin);
            if (uri.getHost() == null || origin.contains("*") || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || !("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))
                    || (uri.getPath() != null && !uri.getPath().isEmpty())) {
                throw new IllegalArgumentException("Configure explicit CORS origins without paths or wildcards");
            }
        }
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(origins);
        cors.setAllowCredentials(true);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-App-Subdomain"));
        cors.setMaxAge(600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
