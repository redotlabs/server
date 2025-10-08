package redot.redot_server.global.jwt.cookie;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final boolean secure;
    private final String domain;

    public CookieUtil(@Value("${jwt.cookie.secure:true}") boolean secure, @Value("${jwt.cookie.domain:.redot.me}") String domain) {
        this.secure = secure;
        this.domain = domain;
    }


    public ResponseCookie createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite("none")
                .path("/")
                .maxAge(0)
                .build();
    }
}
