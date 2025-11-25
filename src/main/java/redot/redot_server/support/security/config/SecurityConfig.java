package redot.redot_server.support.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import redot.redot_server.support.security.filter.redotapp.RedotAppFilter;
import redot.redot_server.support.security.handler.JsonAccessDeniedHandler;
import redot.redot_server.support.security.handler.JsonAuthenticationEntryPoint;
import redot.redot_server.support.security.filter.jwt.auth.AdminJwtAuthenticationFilter;
import redot.redot_server.support.security.filter.jwt.auth.RedotAppJwtAuthenticationFilter;
import redot.redot_server.support.security.filter.jwt.refresh.AdminRefreshTokenFilter;
import redot.redot_server.support.security.filter.jwt.refresh.RedotAppRefreshTokenFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;

    private void applyCommonSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler)
                );
    }

    @Bean
    @Order(-1)
    public SecurityFilterChain publicDomainChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/domain/subdomain", "/api/v1/redot/*")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(0)
    public SecurityFilterChain redotAppRefreshChain(HttpSecurity http,
                                                    RedotAppFilter redotAppFilter,
                                                    RedotAppRefreshTokenFilter redotAppRefreshTokenFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/app/cms/reissue")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class)
                .addFilterAfter(redotAppRefreshTokenFilter, RedotAppFilter.class);
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminRefreshChain(HttpSecurity http,
                                                 AdminRefreshTokenFilter adminRefreshTokenFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/admin/reissue")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(adminRefreshTokenFilter, LogoutFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain redotAppPublicChain(HttpSecurity http,
                                                  RedotAppFilter redotAppFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/app")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class);
        return http.build();
    }
    @Bean
    @Order(3)
    public SecurityFilterChain redotAppApiChain(HttpSecurity http,
                                                RedotAppFilter redotAppFilter,
                                                RedotAppJwtAuthenticationFilter redotAppJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/app/**", "/api/v1/auth/app/cms/me")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class)
                .addFilterBefore(redotAppJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain redotAppAuthChain(HttpSecurity http,
                                                 RedotAppFilter redotAppFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/app/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class);
        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain adminApiChain(HttpSecurity http,
                                             AdminJwtAuthenticationFilter adminJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/admin/**", "/api/v1/auth/admin/impersonation/**", "/api/v1/auth/admin/me")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(adminJwtAuthenticationFilter, LogoutFilter.class);
        return http.build();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain adminAuthChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/admin/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
