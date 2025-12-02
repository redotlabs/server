package redot.redot_server.support.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Profile("dev")
public class CookieProviderDev implements CookieProvider {

    private final boolean secure;
    private final String sameSite;

    public CookieProviderDev(@Value("${jwt.cookie.secure}") boolean secure,
                             @Value("${jwt.cookie.same-site}") String sameSite) {
        this.secure = secure;
        this.sameSite = sameSite;
    }

    private String resolveDomain(HttpServletRequest request) {
        String host = request.getHeader("Origin");
        if (!StringUtils.hasText(host)) {
            return "localhost";
        }
        if (host.contains("localhost")) {
            return "localhost";
        }
        if (host.contains("lvh.me")) {
            return ".lvh.me";
        }
        if (host.contains("redotlabs.me")) {
            return ".redotlabs.me";
        }
        if(host.contains("redot.me")) {
            return ".redot.me";
        }
        return ".redotlabs.vercel.app";
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
