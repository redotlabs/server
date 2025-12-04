package redot.redot_server.global.security.social.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.jwt.token.TokenType;

@Getter
@RequiredArgsConstructor
public enum SocialLoginFlow {
    REDOT_MEMBER("redot-member", TokenType.REDOT_MEMBER);

    private final String flowKey;
    private final TokenType tokenType;

    public static SocialLoginFlow from(String flowKey) {
        return Arrays.stream(values())
                .filter(flow -> flow.flowKey.equals(flowKey))
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_LOGIN_FLOW));
    }
}
