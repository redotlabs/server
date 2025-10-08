package redot.redot_server.global.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redot.redot_server.global.customer.filter.CustomerFilter;
import redot.redot_server.global.security.filter.AdminJwtAuthenticationFilter;
import redot.redot_server.global.security.filter.CustomerJwtAuthenticationFilter;

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
}
