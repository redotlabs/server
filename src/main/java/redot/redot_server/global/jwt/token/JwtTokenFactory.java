package redot.redot_server.global.jwt.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redot.redot_server.global.jwt.provider.JwtProvider;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {
    private final JwtProvider jwtProvider;

    public String createAccessToken(TokenContext context) {
        return jwtProvider.createAccessToken(
                context.id(),
                context.tokenType().getType(),
                context.resolvedRoles(),
                context.customerId()
        );
    }

    public String createRefreshToken(TokenContext context) {
        return jwtProvider.createRefreshToken(
                context.id(),
                context.tokenType().getType(),
                context.resolvedRoles(),
                context.customerId()
        );
    }
}
