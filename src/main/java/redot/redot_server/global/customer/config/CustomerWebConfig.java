package redot.redot_server.global.customer.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redot.redot_server.global.customer.resolver.CustomerArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class CustomerWebConfig implements WebMvcConfigurer {

    private final CustomerArgumentResolver customerArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customerArgumentResolver);
    }
}
