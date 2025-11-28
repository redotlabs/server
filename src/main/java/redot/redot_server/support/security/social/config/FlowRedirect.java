package redot.redot_server.support.security.social.config;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import java.util.Objects;
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
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .anyMatch(allowed -> allowed.equals(host.toLowerCase()));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
