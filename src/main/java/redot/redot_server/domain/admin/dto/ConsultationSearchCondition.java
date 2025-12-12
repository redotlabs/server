package redot.redot_server.domain.admin.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationSearchCondition(
        String email,
        String phone,
        ConsultationStatus status,
        ConsultationType type,
        String currentWebsiteUrl,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
    public static ConsultationSearchCondition empty() {
        return new ConsultationSearchCondition(null, null, null, null, null, null, null);
    }
}
