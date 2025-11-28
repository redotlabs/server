package redot.redot_server.support.security.social.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.service.AuthTokenService;
import redot.redot_server.support.jwt.token.TokenContext;
import redot.redot_server.support.security.social.config.AuthRedirectProperties;
import redot.redot_server.support.security.social.model.RedotOAuth2User;
import redot.redot_server.support.util.CookieUtils;

@Component
@RequiredArgsConstructor
public class RedotOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthTokenService authTokenService;
    private final AuthRedirectProperties authRedirectProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        RedotOAuth2User principal = (RedotOAuth2User) authentication.getPrincipal();

        TokenContext context = new TokenContext(
                principal.getMemberId(),
                principal.getTokenType(),
                null,
                null
        );

        AuthResult authResult = authTokenService.issueTokens(request, context);
        response.addHeader(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString());

        String redirectUri = CookieUtils.getCookie(request, authRedirectProperties.getRedirectCookie().name())
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .orElse(null);

        String target = authRedirectProperties.validateOrDefaultSuccess(principal.getFlowKey(), redirectUri);

        CookieUtils.deleteCookie(response, authRedirectProperties.getRedirectCookie().name(), request.isSecure());
        CookieUtils.deleteCookie(response, authRedirectProperties.getFailureCookie().name(), request.isSecure());

        response.sendRedirect(target);
    }
}
