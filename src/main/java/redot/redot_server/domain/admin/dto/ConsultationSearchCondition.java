package redot.redot_server.domain.admin.dto;

import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationSearchCondition(
        String email,
        String phone,
        ConsultationStatus status,
        ConsultationType type,
        String currentWebsiteUrl
) {
}
