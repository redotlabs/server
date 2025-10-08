package redot.redot_server.global.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.global.customer.context.CustomerContextHolder;
import redot.redot_server.global.jwt.exception.JwtValidationException;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

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
            throw new JwtValidationException("Customer context missing in token");
        }

        if (!contextCustomerId.equals(tokenCustomerId)) {
            throw new JwtValidationException("Customer token mismatch");
        }
    }
}
