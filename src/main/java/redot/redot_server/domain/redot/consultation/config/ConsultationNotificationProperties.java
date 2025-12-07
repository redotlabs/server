package redot.redot_server.domain.redot.consultation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications.consultation")
public record ConsultationNotificationProperties(
        String adminConsoleBaseUrl,
        Discord discord
) {
    public record Discord(String webhookUrl) {}
}
