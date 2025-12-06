package redot.redot_server.global.security.social.config;

public record RedirectCookieProperties(
        String name, int maxAgeSeconds
) {
    public RedirectCookieProperties {
        if (maxAgeSeconds <= 0) {
            maxAgeSeconds = 300;
        }
    }
}
