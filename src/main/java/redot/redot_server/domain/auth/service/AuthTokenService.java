package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.support.jwt.cookie.TokenCookieFactory;
import redot.redot_server.support.jwt.token.JwtTokenFactory;
import redot.redot_server.support.jwt.token.TokenContext;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final JwtTokenFactory jwtTokenFactory;
    private final TokenCookieFactory tokenCookieFactory;

    public AuthResult issueTokens(HttpServletRequest request, TokenContext context) {
        String accessToken = jwtTokenFactory.createAccessToken(context);
        String refreshToken = jwtTokenFactory.createRefreshToken(context);

        ResponseCookie accessTokenCookie = tokenCookieFactory.createAccessTokenCookie(request, context, accessToken);
        ResponseCookie refreshTokenCookie = tokenCookieFactory.createRefreshTokenCookie(request, context, refreshToken);

        return new AuthResult(
                accessTokenCookie,
                refreshTokenCookie,
                new TokenResponse(accessToken)
        );
    }


}
