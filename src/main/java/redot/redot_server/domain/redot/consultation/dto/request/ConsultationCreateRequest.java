package redot.redot_server.domain.redot.consultation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

public record ConsultationCreateRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "전화번호를 입력해주세요.")
        String phone,
        @NotBlank(message = "상담 내용을 입력해주세요.")
        @Size(max = 1000, message = "상담 내용은 1000자를 초과할 수 없습니다")
        String content,
        String page,
        String currentWebsiteUrl,
        @NotNull(message = "상담 타입을 선택해주세요.")
        ConsultationType type
) {
}
