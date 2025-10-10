package redot.redot_server.global.jwt.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenContext;

@Component
@RequiredArgsConstructor
public class TokenCookieFactory {
    private final CookieUtil cookieUtil;
    private final JwtProvider jwtProvider;

    public ResponseCookie createAccessTokenCookie(TokenContext context, String token) {
        return cookieUtil.createCookie(
                context.tokenType().getAccessCookieName(),
                token,
                jwtProvider.getAccessTokenExpiration()
        );
    }

    public ResponseCookie createRefreshTokenCookie(TokenContext context, String token) {
        return cookieUtil.createCookie(
                context.tokenType().getRefreshCookieName(),
                token,
                jwtProvider.getRefreshTokenExpiration()
        );
    }

    public ResponseCookie deleteAccessTokenCookie(String name) {
        return cookieUtil.deleteCookie(name);
    }

    public ResponseCookie deleteRefreshTokenCookie(String name) {
        return cookieUtil.deleteCookie(name);
    }
}
