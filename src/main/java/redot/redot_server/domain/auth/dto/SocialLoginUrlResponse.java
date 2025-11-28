package redot.redot_server.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SocialLoginUrlResponse(
        @Schema(description = "소셜 로그인 인가 요청에 사용할 URL")
        String url
) {}
