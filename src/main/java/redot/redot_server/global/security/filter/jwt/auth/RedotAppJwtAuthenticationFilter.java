package redot.redot_server.global.security.filter.jwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.global.redotapp.context.RedotAppContextHolder;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Component
public class RedotAppJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public RedotAppJwtAuthenticationFilter(JwtProvider jwtProvider,
                                           AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtProvider, authenticationEntryPoint, TokenType.CMS);
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long contextRedotAppId = RedotAppContextHolder.get();
        Long tokenRedotAppId = extractRedotAppId(claims.get("redot_app_id"));

        if (contextRedotAppId == null || tokenRedotAppId == null) {
            throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
        }

        if (!contextRedotAppId.equals(tokenRedotAppId)) {
            throw new AuthException(AuthErrorCode.REDOT_APP_TOKEN_MISMATCH);
        }
    }
}
