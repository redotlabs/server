package redot.redot_server.domain.admin.dto.request;

import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationUpdateRequest(
        String email,
        String phone,
        String content,
        String page,
        String currentWebsiteUrl,
        ConsultationStatus status,
        ConsultationType type
) {
}
