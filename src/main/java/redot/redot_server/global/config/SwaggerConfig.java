package redot.redot_server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
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
}
