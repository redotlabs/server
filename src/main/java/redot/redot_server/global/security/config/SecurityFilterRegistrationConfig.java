package redot.redot_server.global.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redot.redot_server.global.security.filter.redotapp.RedotAppFilter;
import redot.redot_server.global.security.filter.jwt.auth.AdminJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.auth.RedotAppJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.auth.RedotMemberJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.refresh.AdminRefreshTokenFilter;
import redot.redot_server.global.security.filter.jwt.refresh.RedotAppRefreshTokenFilter;
import redot.redot_server.global.security.filter.jwt.refresh.RedotMemberRefreshTokenFilter;

@Configuration
public class SecurityFilterRegistrationConfig {

    @Bean
    public FilterRegistrationBean<RedotAppFilter> redotAppFilterRegistration(RedotAppFilter filter) {
        FilterRegistrationBean<RedotAppFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AdminJwtAuthenticationFilter> adminJwtFilterRegistration(AdminJwtAuthenticationFilter filter) {
        FilterRegistrationBean<AdminJwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RedotAppJwtAuthenticationFilter> redotAppJwtFilterRegistration(RedotAppJwtAuthenticationFilter filter) {
        FilterRegistrationBean<RedotAppJwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RedotMemberJwtAuthenticationFilter> redotMemberJwtFilterRegistration(RedotMemberJwtAuthenticationFilter filter) {
        FilterRegistrationBean<RedotMemberJwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RedotAppRefreshTokenFilter> redotAppRefreshFilterRegistration(RedotAppRefreshTokenFilter filter) {
        FilterRegistrationBean<RedotAppRefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AdminRefreshTokenFilter> adminRefreshFilterRegistration(AdminRefreshTokenFilter filter) {
        FilterRegistrationBean<AdminRefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RedotMemberRefreshTokenFilter> redotMemberRefreshFilterRegistration(RedotMemberRefreshTokenFilter filter) {
        FilterRegistrationBean<RedotMemberRefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
