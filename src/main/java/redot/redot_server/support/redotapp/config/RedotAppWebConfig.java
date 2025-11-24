package redot.redot_server.support.redotapp.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redot.redot_server.support.redotapp.resolver.RedotAppArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class RedotAppWebConfig implements WebMvcConfigurer {

    private final RedotAppArgumentResolver redotAppArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(redotAppArgumentResolver);
    }
}
