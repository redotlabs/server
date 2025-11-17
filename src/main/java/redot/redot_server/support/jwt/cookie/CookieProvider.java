package redot.redot_server.support.jwt.cookie;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.http.ResponseCookie;

public interface CookieProvider {
    ResponseCookie createCookie(HttpServletRequest request, String name, String value, Duration maxAge);
    ResponseCookie deleteCookie(HttpServletRequest request, String name);
}
