package redot.redot_server.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmailVerificationSendResponse(
        @Schema(description = "인증 코드 유효 시간(초)")
        long expiresInSeconds,
        @Schema(description = "다음 발송까지 대기해야 하는 시간(초)")
        long resendCooldownSeconds
) {
}
