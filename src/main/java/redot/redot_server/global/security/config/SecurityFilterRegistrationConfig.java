package redot.redot_server.global.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redot.redot_server.global.security.filter.customer.CustomerFilter;
import redot.redot_server.global.security.filter.jwt.auth.AdminJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.auth.CustomerJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.jwt.refresh.AdminRefreshTokenFilter;
import redot.redot_server.global.security.filter.jwt.refresh.CustomerRefreshTokenFilter;

@Configuration
public class SecurityFilterRegistrationConfig {

    @Bean
    public FilterRegistrationBean<CustomerFilter> customerFilterRegistration(CustomerFilter filter) {
        FilterRegistrationBean<CustomerFilter> registration = new FilterRegistrationBean<>(filter);
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
    public FilterRegistrationBean<CustomerJwtAuthenticationFilter> customerJwtFilterRegistration(CustomerJwtAuthenticationFilter filter) {
        FilterRegistrationBean<CustomerJwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CustomerRefreshTokenFilter> customerRefreshFilterRegistration(CustomerRefreshTokenFilter filter) {
        FilterRegistrationBean<CustomerRefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AdminRefreshTokenFilter> adminRefreshFilterRegistration(AdminRefreshTokenFilter filter) {
        FilterRegistrationBean<AdminRefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
