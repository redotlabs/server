package redot.redot_server.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmailVerificationVerifyResponse(
        @Schema(description = "후속 요청에 사용할 이메일 인증 토큰")
        String verificationToken,
        @Schema(description = "토큰 유효 시간(초)")
        long expiresInSeconds
) {
}
