package redot.redot_server.support.security.filter.jwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenType;

@Component
public class AdminJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public AdminJwtAuthenticationFilter(JwtProvider jwtProvider,
                                        AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtProvider, authenticationEntryPoint, TokenType.ADMIN);
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        // admin 토큰은 추가 검증 없음
    }
}
