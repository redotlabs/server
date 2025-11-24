package redot.redot_server.support.jwt.token;

import java.util.List;

public record TokenContext(
        Long id,
        TokenType tokenType,
        List<String> roles,
        Long redotAppId
) {
    public List<String> resolvedRoles() {
        if (roles == null || roles.isEmpty()) {
            return tokenType.getDefaultRoles();
        }
        return roles;
    }
}
