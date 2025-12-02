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
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import redot.redot_server.support.security.filter.redotapp.RedotAppFilter;
import redot.redot_server.support.security.handler.JsonAccessDeniedHandler;
import redot.redot_server.support.security.handler.JsonAuthenticationEntryPoint;
import redot.redot_server.support.security.filter.jwt.auth.AdminJwtAuthenticationFilter;
import redot.redot_server.support.security.filter.jwt.auth.RedotAppJwtAuthenticationFilter;
import redot.redot_server.support.security.filter.jwt.auth.RedotMemberJwtAuthenticationFilter;
import redot.redot_server.support.security.filter.jwt.refresh.AdminRefreshTokenFilter;
import redot.redot_server.support.security.filter.jwt.refresh.RedotAppRefreshTokenFilter;
import redot.redot_server.support.security.filter.jwt.refresh.RedotMemberRefreshTokenFilter;
import redot.redot_server.support.security.social.SocialAuthorizationEndpoints;
import redot.redot_server.support.security.social.filter.OAuth2RedirectCaptureFilter;
import redot.redot_server.support.security.social.handler.RedotOAuth2FailureHandler;
import redot.redot_server.support.security.social.handler.RedotOAuth2SuccessHandler;
import redot.redot_server.support.security.social.resolver.FlowAwareRedirectUriResolver;
import redot.redot_server.support.security.social.service.RedotOAuth2UserService;
import redot.redot_server.support.security.social.service.RedotOidcUserService;

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
    @Order(-2)
    public SecurityFilterChain socialLoginChain(HttpSecurity http,
                                                OAuth2RedirectCaptureFilter redirectCaptureFilter,
                                                RedotOAuth2UserService redotOAuth2UserService,
                                                RedotOidcUserService redotOidcUserService,
                                                RedotOAuth2SuccessHandler redotOAuth2SuccessHandler,
                                                RedotOAuth2FailureHandler redotOAuth2FailureHandler,
                                                FlowAwareRedirectUriResolver flowAwareRedirectUriResolver) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher(
                        SocialAuthorizationEndpoints.API_AUTHORIZATION_BASE_URI + "/**",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/api/v1/sign-in/*/social/callback/**"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redirectCaptureFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri(SocialAuthorizationEndpoints.API_AUTHORIZATION_BASE_URI)
                                .authorizationRequestResolver(flowAwareRedirectUriResolver))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/api/v1/sign-in/*/social/callback/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(redotOAuth2UserService)
                                .oidcUserService(redotOidcUserService))
                        .successHandler(redotOAuth2SuccessHandler)
                        .failureHandler(redotOAuth2FailureHandler)
                );
        return http.build();
    }

    @Bean
    @Order(-1)
    public SecurityFilterChain publicDomainChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/domain/subdomain", "/api/v1/app/plans")
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
    public SecurityFilterChain redotMemberAppChain(HttpSecurity http,
                                                   RedotMemberJwtAuthenticationFilter redotMemberJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher(request -> {
                    String uri = request.getRequestURI();
                    String method = request.getMethod();
                    return (uri.equals("/api/v1/app") && (method.equals("POST") || method.equals("GET")))
                            || (uri.matches("/api/v1/app/\\d+/create-manager") && method.equals("POST"));
                })
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(redotMemberJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain redotAppPublicChain(HttpSecurity http,
                                                   RedotAppFilter redotAppFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/app/by-subdomain")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class);
        return http.build();
    }
    @Bean
    @Order(4)
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
    @Order(5)
    public SecurityFilterChain redotAppAuthChain(HttpSecurity http,
                                                 RedotAppFilter redotAppFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/app/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotAppFilter, SecurityContextHolderFilter.class);
        return http.build();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain adminApiChain(HttpSecurity http,
                                             AdminJwtAuthenticationFilter adminJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/admin/**", "/api/v1/auth/admin/impersonation/**", "/api/v1/auth/admin/me")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(adminJwtAuthenticationFilter, LogoutFilter.class);
        return http.build();
    }

    @Bean
    @Order(7)
    public SecurityFilterChain adminAuthChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/admin/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(8)
    public SecurityFilterChain redotMemberRefreshChain(HttpSecurity http,
                                                       RedotMemberRefreshTokenFilter redotMemberRefreshTokenFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/redot/member/reissue")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(redotMemberRefreshTokenFilter, LogoutFilter.class);
        return http.build();
    }

    @Bean
    @Order(9)
    public SecurityFilterChain redotMemberApiChain(HttpSecurity http,
                                                   RedotMemberJwtAuthenticationFilter redotMemberJwtAuthenticationFilter) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/redot/member/me")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(redotMemberJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(10)
    public SecurityFilterChain redotMemberAuthChain(HttpSecurity http) throws Exception {
        applyCommonSecurity(http);
        http.securityMatcher("/api/v1/auth/redot/member/**", "/api/v1/auth/email-verification/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
