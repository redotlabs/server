package redot.redot_server.domain.redot.consultation.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.redot.consultation.entity.Consultation;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationResponse(
        Long id,
        String email,
        String phone,
        String content,
        String page,
        String currentWebsiteUrl,
        String remark,
        ConsultationStatus status,
        ConsultationType type,
        LocalDateTime createdAt
) {
    public static ConsultationResponse fromEntity(Consultation consultation) {
        return new ConsultationResponse(
                consultation.getId(),
                consultation.getEmail(),
                consultation.getPhone(),
                consultation.getContent(),
                consultation.getPage(),
                consultation.getCurrentWebsiteUrl(),
                consultation.getRemark(),
                consultation.getStatus(),
                consultation.getType(),
                consultation.getCreatedAt()
        );
    }
}
