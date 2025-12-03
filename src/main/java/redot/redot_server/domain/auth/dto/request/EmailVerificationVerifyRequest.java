package redot.redot_server.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationVerifyRequest(
        @Schema(description = "인증 대상 이메일")
        @Email
        @NotBlank
        String email,
        @Schema(description = "인증 용도 (예: redot-member-sign-up)")
        @NotBlank
        String purpose,
        @Schema(description = "수신한 인증 코드")
        @NotBlank
        String code
) {
}
