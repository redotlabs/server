package redot.redot_server.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;

public record EmailVerificationSendRequest(
        @Schema(description = "인증 코드를 받을 이메일")
        @Email
        @NotBlank
        String email,
        @Schema(description = "인증 용도")
        @NotNull
        EmailVerificationPurpose purpose,
        @Schema(description = "CMS 멤버 비밀번호 초기화 시 사용할 RedotApp의 Subdomain", nullable = true)
        String redotAppSubdomain
) {
}
