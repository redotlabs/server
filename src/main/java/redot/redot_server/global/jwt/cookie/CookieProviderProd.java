package redot.redot_server.global.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class CookieProviderProd implements CookieProvider{

    private final boolean secure;
    private final String domain;
    private final String sameSite;

    public CookieProviderProd(@Value("${jwt.cookie.secure:true}") boolean secure, @Value("${jwt.cookie.domain:.redot.me}") String domain, @Value("${jwt.cookie.same-site}") String sameSite) {
        this.secure = secure;
        this.domain = domain;
        this.sameSite = sameSite;
    }


    public ResponseCookie createCookie(HttpServletRequest request, String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie deleteCookie(HttpServletRequest request, String name) {
        return ResponseCookie.from(name, "")
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
    }
}
