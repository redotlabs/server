package redot.redot_server.domain.redot.consultation.notification;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import redot.redot_server.domain.redot.consultation.config.ConsultationNotificationProperties;
import redot.redot_server.domain.redot.consultation.entity.Consultation;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordConsultationNotificationChannel implements ConsultationNotificationChannel {

    private static final DateTimeFormatter CREATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RestTemplateBuilder restTemplateBuilder;
    private final ConsultationNotificationProperties properties;

    @Override
    public void send(Consultation consultation) {
        ConsultationNotificationProperties.Discord discord = properties.discord();
        if (discord == null || !StringUtils.hasText(discord.webhookUrl())) {
            return;
        }

        RestTemplate restTemplate = restTemplateBuilder.build();
        Map<String, Object> payload = buildPayload(consultation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(discord.webhookUrl(), entity, Void.class);
        } catch (Exception ex) {
            log.error("Failed to send Discord consultation notification", ex);
        }
    }

    private Map<String, Object> buildPayload(Consultation consultation) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "📩 **새 상담이 접수되었습니다.**");

        Map<String, Object> embed = new HashMap<>();
        embed.put("title", "Consultation #" + consultation.getId());
        embed.put("url", buildConsultationUrl(consultation));
        embed.put("description", "유형: **" + translateType(consultation) + "**");
        embed.put("color", 0x3498DB);

        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(field("이메일 ✉️", consultation.getEmail(), true));
        fields.add(field("연락처 📞", consultation.getPhone(), true));
        fields.add(field("기존 웹사이트 주소 🌐", consultation.getCurrentWebsiteUrl(), true));
        fields.add(field("상담 관리 🔗", buildConsultationUrl(consultation), true));
        fields.add(field("내용", formatContent(consultation.getContent()), false));
        embed.put("fields", fields);

        Map<String, Object> footer = new HashMap<>();
        footer.put("text", "생성일시: " + formatCreatedAt(consultation));
        embed.put("footer", footer);

        payload.put("embeds", List.of(embed));
        return payload;
    }

    private Map<String, Object> field(String name, String value, boolean inline) {
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("value", StringUtils.hasText(value) ? value : "-");
        field.put("inline", inline);
        return field;
    }

    private String buildConsultationUrl(Consultation consultation) {
        if (!StringUtils.hasText(properties.adminConsoleBaseUrl())) {
            return null;
        }
        return properties.adminConsoleBaseUrl() + "/consultation?id=" + consultation.getId();
    }

    private String formatCreatedAt(Consultation consultation) {
        if (consultation.getCreatedAt() == null) {
            return "-";
        }
        return CREATED_AT_FORMATTER.format(consultation.getCreatedAt());
    }

    private String formatContent(String content) {
        if (!StringUtils.hasText(content)) {
            return "-";
        }
        if (content.length() > 1000) {
            content = content.substring(0, 997) + "...";
        }
        return "```" + content + "```";
    }

    private String translateType(Consultation consultation) {
        return switch (consultation.getType()) {
            case NEW -> "신규 제작";
            case RENEWAL -> "리뉴얼";
        };
    }

}
