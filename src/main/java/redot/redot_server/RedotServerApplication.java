package redot.redot_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import redot.redot_server.global.email.EmailVerificationProperties;
import redot.redot_server.global.security.social.config.AuthRedirectProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthRedirectProperties.class, EmailVerificationProperties.class})
public class RedotServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedotServerApplication.class, args);
    }

}
