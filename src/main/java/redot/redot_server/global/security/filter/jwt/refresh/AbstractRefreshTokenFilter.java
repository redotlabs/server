package redot.redot_server.global.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.jwt.cookie.CookieProvider;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@RequiredArgsConstructor
abstract class AbstractRefreshTokenFilter extends OncePerRequestFilter {

    private static final String AUTH_EXCEPTION_ATTR = AuthException.class.getName();

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String refreshToken = extractRefreshToken(request);
        if (!StringUtils.hasText(refreshToken)) {
            commenceFailure(request, response, new AuthException(AuthErrorCode.EMPTY_TOKEN));
            return;
        }

        try {
            Claims claims = jwtProvider.parseToken(refreshToken);
            validateTokenType(claims);
            validateClaims(claims, request);

            RefreshTokenPayload payload = buildPayload(refreshToken, claims);
            RefreshTokenPayloadHolder.set(request, payload);

            filterChain.doFilter(request, response);
        } catch (AuthException authEx) {
            commenceFailure(request, response, authEx);
        } catch (JwtException jwtEx) {
            commenceFailure(request, response, new AuthException(AuthErrorCode.INVALID_TOKEN, jwtEx));
        }
    }

    private RefreshTokenPayload buildPayload(String rawToken, Claims claims) {
        Long subjectId = Long.valueOf(claims.getSubject());
        Long customerId = extractCustomerId(claims.get("customer_id"));
        List<String> roles = extractRoles(claims.get("roles"));
        return new RefreshTokenPayload(rawToken, requiredTokenType(), subjectId, customerId, roles);
    }

    private void commenceFailure(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthException authException) throws IOException, ServletException {
        expireCookies(request, response);
        SecurityContextHolder.clearContext();
        request.setAttribute(AUTH_EXCEPTION_ATTR, authException);
        authenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(authException.getMessage(), authException)
        );
    }

    private void expireCookies(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie deleteAccess = cookieProvider.deleteCookie(request, requiredTokenType().getAccessCookieName());
        ResponseCookie deleteRefresh = cookieProvider.deleteCookie(request, requiredTokenType().getRefreshCookieName());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> requiredTokenType().getRefreshCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private void validateTokenType(Claims claims) {
        String type = claims.get("type", String.class);
        if (!requiredTokenType().getType().equalsIgnoreCase(type)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }
    }

    protected Long extractCustomerId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(raw.toString());
    }

    protected List<String> extractRoles(Object raw) {
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    protected abstract TokenType requiredTokenType();

    protected abstract void validateClaims(Claims claims, HttpServletRequest request);
}
