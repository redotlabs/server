package redot.redot_server.global.security.filter.jwt.refresh;

import java.util.List;
import redot.redot_server.global.jwt.token.TokenType;

public record RefreshTokenPayload(
        String refreshToken,
        TokenType tokenType,
        Long subjectId,
        Long redotAppId,
        List<String> roles
) {}
