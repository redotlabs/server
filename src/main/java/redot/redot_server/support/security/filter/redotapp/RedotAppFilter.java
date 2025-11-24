package redot.redot_server.support.security.filter.redotapp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.redotapp.entity.RedotAppStatus;
import redot.redot_server.support.redotapp.context.RedotAppContextHolder;

@Component
@RequiredArgsConstructor
public class RedotAppFilter extends OncePerRequestFilter {

    private final DomainRepository domainRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String AUTH_EXCEPTION_ATTR = AuthException.class.getName();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String subdomain = request.getHeader("X-RedotApp-Subdomain");
            if (!StringUtils.hasText(subdomain)) {
                throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
            }

            Long redotAppId = resolveRedotAppId(subdomain);
            if (redotAppId == null) {
                throw new AuthException(AuthErrorCode.REDOT_APP_DOMAIN_NOT_FOUND);
            }

            RedotAppContextHolder.set(redotAppId);
            filterChain.doFilter(request, response);
        } catch (AuthException authException) {
            commenceFailure(request, response, authException);
        } finally {
            RedotAppContextHolder.clear();
        }
    }

    private Long resolveRedotAppId(String subDomain) {
        Optional<Domain> domainLookup = domainRepository.findBySubdomain(subDomain);
        Domain resolvedDomain = domainLookup.orElse(null);

        if (resolvedDomain == null || Boolean.TRUE.equals(resolvedDomain.getReserved())) {
            return null;
        }

        RedotApp redotApp = resolvedDomain.getRedotApp();
        if (redotApp == null || redotApp.getId() == null) {
            return null;
        }

        if (redotApp.getStatus() != RedotAppStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.REDOT_APP_INACTIVE);
        }

        return redotApp.getId();
    }

    private void commenceFailure(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthException authException) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.setAttribute(AUTH_EXCEPTION_ATTR, authException);
        authenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(authException.getMessage(), authException)
        );
    }
}
