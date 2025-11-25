package redot.redot_server.support.security.filter.jwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenType;

@Component
public class RedotMemberJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public RedotMemberJwtAuthenticationFilter(JwtProvider jwtProvider,
                                              AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtProvider, authenticationEntryPoint, TokenType.REDOT_MEMBER);
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        // No additional validation for RedotMember access tokens
    }
}
