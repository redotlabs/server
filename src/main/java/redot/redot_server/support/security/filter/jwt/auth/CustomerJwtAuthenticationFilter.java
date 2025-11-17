package redot.redot_server.support.security.filter.jwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.support.customer.context.CustomerContextHolder;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Component
public class CustomerJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public CustomerJwtAuthenticationFilter(JwtProvider jwtProvider,
                                           AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtProvider, authenticationEntryPoint, TokenType.CMS);
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long contextCustomerId = CustomerContextHolder.get();
        Long tokenCustomerId = extractCustomerId(claims.get("customer_id"));

        if (contextCustomerId == null || tokenCustomerId == null) {
            throw new AuthException(AuthErrorCode.CUSTOMER_CONTEXT_REQUIRED);
        }

        if (!contextCustomerId.equals(tokenCustomerId)) {
            throw new AuthException(AuthErrorCode.CUSTOMER_TOKEN_MISMATCH);
        }
    }
}
