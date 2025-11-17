package redot.redot_server.support.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenContext;

@Component
@RequiredArgsConstructor
public class TokenCookieFactory {
    private final CookieProvider cookieProvider;
    private final JwtProvider jwtProvider;

    public ResponseCookie createAccessTokenCookie(HttpServletRequest request, TokenContext context, String token) {
        return cookieProvider.createCookie(
                request,
                context.tokenType().getAccessCookieName(),
                token,
                jwtProvider.getAccessTokenExpiration()
        );
    }

    public ResponseCookie createRefreshTokenCookie(HttpServletRequest request, TokenContext context, String token) {
        return cookieProvider.createCookie(
                request,
                context.tokenType().getRefreshCookieName(),
                token,
                jwtProvider.getRefreshTokenExpiration()
        );
    }

    public ResponseCookie deleteAccessTokenCookie(HttpServletRequest request, String name) {
        return cookieProvider.deleteCookie(request, name);
    }

    public ResponseCookie deleteRefreshTokenCookie(HttpServletRequest request, String name) {
        return cookieProvider.deleteCookie(request, name);
    }
}
