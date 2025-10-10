package redot.redot_server.global.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.global.jwt.cookie.CookieUtil;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@Component
public class AdminRefreshTokenFilter extends AbstractRefreshTokenFilter {

    public AdminRefreshTokenFilter(JwtProvider jwtProvider,
                                   CookieUtil cookieUtil,
                                   AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtProvider, cookieUtil, authenticationEntryPoint);
    }

    @Override
    protected TokenType requiredTokenType() {
        return TokenType.ADMIN;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        // Admin 토큰은 추가 검증이 필요하지 않습니다.
    }
}
