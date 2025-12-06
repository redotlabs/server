package redot.redot_server.domain.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationUpdateRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        String phone,
        @NotBlank(message = "상담 내용을 입력해주세요.")
        @Size(max = 1000, message = "상담 내용은 1000자를 초과할 수 없습니다")
        String content,
        String page,
        String currentWebsiteUrl,
        @NotNull(message = "상담 상태를 선택해주세요.")
        ConsultationStatus status,
        @NotNull(message = "상담 타입을 선택해주세요.")
        ConsultationType type
) {
}
