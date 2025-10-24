package redot.redot_server.global.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class CookieProviderDev implements CookieProvider{

    private final boolean secure;
    private final String sameSite;

    public CookieProviderDev(@Value("${jwt.cookie.secure}") boolean secure, @Value("${jwt.cookie.same-site}")  String sameSite) {
        this.secure = secure;
        this.sameSite = sameSite;
    }

    private String resolveDomain(HttpServletRequest request) {
        System.out.println(request.getHeader("X-Forwarded-Host"));
        System.out.println(request.getHeader("X-Original-Host"));
        System.out.println(request.getHeader("Host"));
        String host = request.getHeader("X-Original-Host");
        if (host.contains("localhost")) return "localhost";
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
