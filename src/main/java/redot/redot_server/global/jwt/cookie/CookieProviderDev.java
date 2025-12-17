package redot.redot_server.global.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Profile("dev")
@Slf4j
public class CookieProviderDev implements CookieProvider {

    private final boolean secure;
    private final String sameSite;

    public CookieProviderDev(@Value("${jwt.cookie.secure}") boolean secure,
                             @Value("${jwt.cookie.same-site}") String sameSite) {
        this.secure = secure;
        this.sameSite = sameSite;
    }

    private String resolveDomain(HttpServletRequest request) {
        String host = extractHost(request);
        if (!StringUtils.hasText(host)) {
            return "localhost";
        }

        String normalized = host.toLowerCase();
        if (normalized.contains("localhost")) {
            return "localhost";
        }
        if (normalized.contains("lvh.me")) {
            return ".lvh.me";
        }
        if (normalized.contains("redotlabs.me")) {
            return ".redotlabs.me";
        }
        if (normalized.contains("redot.me")) {
            return ".redot.me";
        }
        return ".redotlabs.me";
    }

    private String extractHost(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        String hostCandidate = firstNonEmpty(origin, referer);
        if (!StringUtils.hasText(hostCandidate)) {
            return request.getServerName();
        }
        return stripScheme(hostCandidate);
    }

    private String stripScheme(String value) {
        String trimmed = value.trim();
        int schemeEnd = trimmed.indexOf("//");
        if (schemeEnd >= 0) {
            return trimmed.substring(schemeEnd + 2);
        }
        return trimmed;
    }

    private String firstNonEmpty(String... candidates) {
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public ResponseCookie createCookie(HttpServletRequest request, String name, String value, Duration maxAge) {
        String requestDomain = resolveDomain(request);
        return ResponseCookie.from(name, value)
                .domain(requestDomain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie deleteCookie(HttpServletRequest request, String name) {
        String requestDomain = resolveDomain(request);
        return ResponseCookie.from(name, "")
                .domain(requestDomain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
    }
}
