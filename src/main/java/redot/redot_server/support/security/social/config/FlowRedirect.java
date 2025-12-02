package redot.redot_server.support.security.social.config;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Locale;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public record FlowRedirect(
        @NotBlank String defaultSuccessRedirect,
        @NotBlank String defaultFailureRedirect,
        List<String> allowedRedirectHosts
) {

    public boolean isAllowed(String uri) {
        try {
            URI parsed = URI.create(uri);
            String host = parsed.getHost();
            if (!StringUtils.hasText(host)) {
                return false;
            }
            if (CollectionUtils.isEmpty(allowedRedirectHosts)) {
                return false;
            }
            return allowedRedirectHosts.stream()
                    .filter(StringUtils::hasText)
                    .anyMatch(allowed -> matchesHost(allowed, host));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean matchesHost(String allowed, String host) {
        String normalizedAllowed = allowed.toLowerCase(Locale.ROOT).trim();
        String normalizedHost = host.toLowerCase(Locale.ROOT);

        if (normalizedAllowed.startsWith("*.")) {
            String suffix = normalizedAllowed.substring(2);
            if (!StringUtils.hasText(suffix)) {
                return false;
            }
            return normalizedHost.equals(suffix) || normalizedHost.endsWith("." + suffix);
        }

        return normalizedAllowed.equals(normalizedHost);
    }
}
