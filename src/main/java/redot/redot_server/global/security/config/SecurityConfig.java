package redot.redot_server.global.security.config;

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
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import redot.redot_server.global.security.filter.customer.CustomerFilter;
import redot.redot_server.global.security.handler.JsonAccessDeniedHandler;
import redot.redot_server.global.security.handler.JsonAuthenticationEntryPoint;
import redot.redot_server.global.security.filter.jwt.auth.AdminJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.auth.CustomerJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.refresh.AdminRefreshTokenFilter;
import redot.redot_server.global.security.filter.jwt.refresh.CustomerRefreshTokenFilter;

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
    @Order(0)
    public SecurityFilterChain customerRefreshChain(HttpSecurity http,
                                                    CustomerFilter customerFilter,
                                                    CustomerRefreshTokenFilter customerRefreshTokenFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/customer/cms/reissue")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(customerFilter, SecurityContextHolderFilter.class)
                .addFilterAfter(customerRefreshTokenFilter, CustomerFilter.class);
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
    public SecurityFilterChain customerAuthChain(HttpSecurity http,
                                                 CustomerFilter customerFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/customer/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(customerFilter, SecurityContextHolderFilter.class);
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain customerApiChain(HttpSecurity http,
                                                CustomerFilter customerFilter,
                                                CustomerJwtAuthenticationFilter customerJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/customer/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(customerFilter, SecurityContextHolderFilter.class)
                .addFilterAfter(customerJwtAuthenticationFilter, CustomerFilter.class);
        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain adminAuthChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/admin/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain adminApiChain(HttpSecurity http,
                                             AdminJwtAuthenticationFilter adminJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/admin/**", "/api/v1/auth/admin/impersonation/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(adminJwtAuthenticationFilter, LogoutFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
