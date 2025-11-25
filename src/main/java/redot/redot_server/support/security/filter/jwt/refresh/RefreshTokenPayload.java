package redot.redot_server.support.security.filter.jwt.refresh;

import java.util.List;
import redot.redot_server.support.jwt.token.TokenType;

public record RefreshTokenPayload(
        String refreshToken,
        TokenType tokenType,
        Long subjectId,
        Long redotAppId,
        List<String> roles
) {}
