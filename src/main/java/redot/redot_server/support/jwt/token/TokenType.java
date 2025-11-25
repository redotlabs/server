package redot.redot_server.support.jwt.token;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ADMIN("admin", "redot_admin_access_token", "redot_admin_refresh_token", List.of("MASTER")),
    CMS("cms", "redot_cms_access_token", "redot_cms_refresh_token", List.of()),
    REDOT_MEMBER("redot_member", "redot_member_access_token", "redot_member_refresh_token", List.of()),
    ;

    private final String type;
    private final String accessCookieName;
    private final String refreshCookieName;
    private final List<String> defaultRoles;

    public static TokenType from(String type) {
        for (TokenType tokenType : values()) {
            if (tokenType.type.equals(type)) {
                return tokenType;
            }
        }
        throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
    }
}
