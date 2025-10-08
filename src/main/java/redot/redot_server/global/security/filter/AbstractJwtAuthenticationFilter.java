package redot.redot_server.global.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import redot.redot_server.global.jwt.exception.JwtValidationException;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@RequiredArgsConstructor
abstract class AbstractJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final TokenType requiredType;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractAccessToken(request);
            if (!StringUtils.hasText(token)) {
                throw new JwtValidationException("Missing access token");
            }

            Claims claims = jwtProvider.parseToken(token);
            validateType(claims);
            validateClaims(claims, request);

            JwtPrincipal principal = createPrincipal(claims);
            Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
            Authentication authentication = createAuthentication(principal, authorities, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtValidationException | JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException(ex.getMessage(), ex)
            );
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> requiredType.getAccessCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private void validateType(Claims claims) {
        String tokenType = claims.get("type", String.class);
        if (tokenType == null || !requiredType.getType().equalsIgnoreCase(tokenType)) {
            throw new JwtValidationException("Invalid token type");
        }
    }

    private JwtPrincipal createPrincipal(Claims claims) {
        Long subjectId = parseSubject(claims.getSubject());
        Long customerId = extractCustomerId(claims.get("customer_id"));
        return new JwtPrincipal(subjectId, requiredType, customerId);
    }

    private Long parseSubject(String subject) {
        if (!StringUtils.hasText(subject)) {
            throw new JwtValidationException("Token subject missing");
        }
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            throw new JwtValidationException("Invalid token subject", ex);
        }
    }

    protected Long extractCustomerId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ex) {
                throw new JwtValidationException("Invalid customer id", ex);
            }
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object rolesClaim = claims.get("roles");
        if (!(rolesClaim instanceof List<?> roles) || roles.isEmpty()) {
            return List.of();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(StringUtils::hasText)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private Authentication createAuthentication(JwtPrincipal principal,
                                                Collection<? extends GrantedAuthority> authorities,
                                                HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new JwtAuthenticationDetails(request.getRemoteAddr(), request.getHeader("User-Agent")));
        return authentication;
    }

    protected void validateClaims(Claims claims, HttpServletRequest request) {}

    protected record JwtPrincipal(Long id, TokenType tokenType, Long customerId) {}

    protected record JwtAuthenticationDetails(String remoteAddress, String userAgent) {}
}
