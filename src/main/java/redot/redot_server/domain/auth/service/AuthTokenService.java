package redot.redot_server.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.JwtTokenFactory;
import redot.redot_server.global.jwt.token.TokenContext;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final JwtTokenFactory jwtTokenFactory;
    private final TokenCookieFactory tokenCookieFactory;

    public AuthResult issueTokens(TokenContext context) {
        String accessToken = jwtTokenFactory.createAccessToken(context);
        String refreshToken = jwtTokenFactory.createRefreshToken(context);

        ResponseCookie accessTokenCookie = tokenCookieFactory.createAccessTokenCookie(context, accessToken);
        ResponseCookie refreshTokenCookie = tokenCookieFactory.createRefreshTokenCookie(context, refreshToken);

        return new AuthResult(
                accessTokenCookie,
                refreshTokenCookie,
                new TokenResponse(accessToken)
        );
    }


}
