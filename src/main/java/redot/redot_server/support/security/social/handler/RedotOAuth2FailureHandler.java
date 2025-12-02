package redot.redot_server.support.security.social.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.security.social.config.AuthRedirectProperties;
import redot.redot_server.support.security.social.model.FlowResolver;
import redot.redot_server.support.security.social.matcher.PathTemplateRequestMatcher;
import redot.redot_server.support.util.CookieUtils;

@Component
@RequiredArgsConstructor
public class RedotOAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final RequestMatcher REDIRECTION_MATCHER =
            new PathTemplateRequestMatcher("/api/v1/sign-in/{flow}/social/callback/{registrationId}");

    private final AuthRedirectProperties authRedirectProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String registrationId = resolveRegistrationId(request);
        String flow = tryResolveFlow(registrationId);

        Optional<Cookie> failureCookie = CookieUtils.getCookie(request, authRedirectProperties.getFailureCookie().name());
        String failureUri = failureCookie.map(Cookie::getValue).filter(StringUtils::hasText).orElse(null);

        String target = authRedirectProperties.validateOrDefaultFailure(flow, failureUri);
        CookieUtils.deleteCookie(response, authRedirectProperties.getRedirectCookie().name(), request.isSecure());
        CookieUtils.deleteCookie(response, authRedirectProperties.getFailureCookie().name(), request.isSecure());

        String redirect = UriComponentsBuilder.fromUriString(target)
                .queryParam("errorCode", exception.getClass().getSimpleName())
                .build(true)
                .toUriString();

        response.sendRedirect(redirect);
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        RequestMatcher.MatchResult matchResult = REDIRECTION_MATCHER.matcher(request);
        if (matchResult.isMatch()) {
            return matchResult.getVariables().get("registrationId");
        }
        return "redot-member-google";
    }

    private String tryResolveFlow(String registrationId) {
        if (!StringUtils.hasText(registrationId)) {
            return "redot-member";
        }
        try {
            return FlowResolver.resolveFlow(registrationId);
        } catch (AuthException ex) {
            return "redot-member";
        }
    }
}
