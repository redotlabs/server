package redot.redot_server.domain.cms.inquiry.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InquiryClockConfig {

    @Bean
    public Clock inquiryClock() {
        return Clock.systemDefaultZone();
    }
}
