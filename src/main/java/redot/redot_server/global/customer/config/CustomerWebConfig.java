package redot.redot_server.global.customer.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redot.redot_server.global.customer.filter.CustomerFilter;
import redot.redot_server.global.customer.resolver.CustomerArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class CustomerWebConfig implements WebMvcConfigurer {

    private final CustomerArgumentResolver customerArgumentResolver;
    private final CustomerFilter customerFilter;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customerArgumentResolver);
    }

    @Bean
    public FilterRegistrationBean<CustomerFilter> customerFilterRegistration() {
        FilterRegistrationBean<CustomerFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(customerFilter);
        registration.setName("customerFilter");
        registration.addUrlPatterns("/customer/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        return registration;
    }
}
