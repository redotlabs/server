package redot.redot_server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server()
                .url("https://redot-test.dhxxn.dev")
                .description("Development Server");
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Server");

        return new OpenAPI()
                .info(new Info().title("Redot API")
                        .version("1.0")
                        .description("Redot Server API Documentation"))
                .servers(List.of(devServer, localServer));
    }

    @Bean
    public GroupedOpenApi cmsApi() {
        return GroupedOpenApi.builder()
                .group("cms")
                .displayName("CMS API")
                .pathsToMatch("/api/v1/app/cms/**", "/api/v1/auth/app/cms/**")
                .build();
    }

    @Bean
    public GroupedOpenApi siteApi() {
        return GroupedOpenApi.builder()
                .group("site")
                .displayName("Site/Public API")
                .pathsToMatch("/api/v1/app/site/**", "/api/v1/domain/**")
                .build();
    }

    @Bean
    public GroupedOpenApi redotApi() {
        return GroupedOpenApi.builder()
                .group("redot")
                .displayName("Redot API")
                .pathsToMatch("/api/v1/app/**", "/api/v1/redot/**", "/api/v1/auth/redot/member/**", "/api/v1/auth/email-verification/**")
                .pathsToExclude("/api/v1/app/cms/**", "/api/v1/app/site/**", "/api/v1/redot/admin/**", "/api/v1/auth/app/cms/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Admin API")
                .pathsToMatch("/api/v1/redot/admin/**", "/api/v1/auth/redot/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("All APIs")
                .pathsToMatch("/**")
                .build();
    }
}
