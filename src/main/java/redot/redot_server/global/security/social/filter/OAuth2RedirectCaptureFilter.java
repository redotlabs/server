package redot.redot_server.global.security.social.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.security.social.SocialAuthorizationEndpoints;
import redot.redot_server.global.security.social.config.AuthRedirectProperties;
import redot.redot_server.global.security.social.model.FlowResolver;
import redot.redot_server.global.security.social.matcher.PathTemplateRequestMatcher;
import redot.redot_server.global.util.CookieUtils;

@Component
@RequiredArgsConstructor
public class OAuth2RedirectCaptureFilter extends OncePerRequestFilter {

    private static final List<PathTemplateRequestMatcher> AUTHORIZATION_MATCHERS =
            SocialAuthorizationEndpoints.SUPPORTED_AUTHORIZATION_BASE_URIS.stream()
                    .map(base -> new PathTemplateRequestMatcher(base + "/{registrationId}"))
                    .toList();

    private final AuthRedirectProperties authRedirectProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RequestMatcher.MatchResult matchResult = match(request);
        if (matchResult == null || !matchResult.isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }

        String registrationId = matchResult.getVariables().get("registrationId");
        String flow;
        try {
            flow = FlowResolver.resolveFlow(registrationId);
        } catch (AuthException ex) {
            response.sendError(ex.getErrorCode().getStatusCode(), ex.getMessage());
            return;
        }

        String requestedRedirect = request.getParameter("redirect_uri");
        String requestedFailure = request.getParameter("failure_uri");

        String finalRedirect = authRedirectProperties.validateOrDefaultSuccess(flow, requestedRedirect);
        String finalFailure = authRedirectProperties.validateOrDefaultFailure(flow, requestedFailure);

        CookieUtils.addHttpOnlyCookie(
                response,
                authRedirectProperties.getRedirectCookie().name(),
                finalRedirect,
                authRedirectProperties.getRedirectCookie().maxAgeSeconds(),
                request.isSecure()
        );

        CookieUtils.addHttpOnlyCookie(
                response,
                authRedirectProperties.getFailureCookie().name(),
                finalFailure,
                authRedirectProperties.getFailureCookie().maxAgeSeconds(),
                request.isSecure()
        );

        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        filterChain.doFilter(request, response);
    }

    private RequestMatcher.MatchResult match(HttpServletRequest request) {
        for (PathTemplateRequestMatcher matcher : AUTHORIZATION_MATCHERS) {
            RequestMatcher.MatchResult result = matcher.matcher(request);
            if (result.isMatch()) {
                return result;
            }
        }
        return RequestMatcher.MatchResult.notMatch();
    }
}
