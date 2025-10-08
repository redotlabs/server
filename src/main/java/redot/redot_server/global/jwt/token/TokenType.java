package redot.redot_server.global.jwt.token;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ADMIN("admin", "redot_admin_access_token", "redot_admin_refresh_token", List.of("MASTER")),
    CMS("cms", "redot_cms_access_token", "redot_cms_refresh_token", List.of()),
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
        throw new IllegalArgumentException("Invalid token type: " + type);
    }
}
