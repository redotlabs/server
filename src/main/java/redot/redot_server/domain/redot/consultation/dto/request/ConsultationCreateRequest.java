package redot.redot_server.domain.redot.consultation.dto.request;

import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationCreateRequest(
        String email,
        String phone,
        String content,
        String page,
        String currentWebsiteUrl,
        ConsultationType type
) {
}
