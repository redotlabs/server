package redot.redot_server.global.security.social.config;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
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
            int port = parsed.getPort();
            if (!StringUtils.hasText(host)) {
                return false;
            }
            if (CollectionUtils.isEmpty(allowedRedirectHosts)) {
                return false;
            }
            return allowedRedirectHosts.stream()
                    .filter(StringUtils::hasText)
                    .anyMatch(allowed -> matchesHostAndPort(allowed, host, port));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean matchesHostAndPort(String allowed, String host, int port) {
        AllowedHost allowedHost = AllowedHost.parse(allowed);
        if (allowedHost == null) {
            return false;
        }

        if (!matchesHost(allowedHost.hostPattern(), host)) {
            return false;
        }

        if (allowedHost.port() == null) {
            return true;
        }
        return port == allowedHost.port();
    }

    private boolean matchesHost(String allowedPattern, String host) {
        String normalizedAllowed = allowedPattern.toLowerCase(Locale.ROOT).trim();
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

    private record AllowedHost(String hostPattern, Integer port) {

        private static AllowedHost parse(String source) {
            String trimmed = source.toLowerCase(Locale.ROOT).trim();
            if (!StringUtils.hasText(trimmed)) {
                return null;
            }

            int colonIndex = trimmed.lastIndexOf(':');
            if (colonIndex <= 0 || colonIndex == trimmed.length() - 1) {
                return new AllowedHost(trimmed, null);
            }

            String hostPart = trimmed.substring(0, colonIndex);
            String portPart = trimmed.substring(colonIndex + 1);

            if (!StringUtils.hasText(hostPart)) {
                return null;
            }

            if (!StringUtils.hasText(portPart)) {
                return new AllowedHost(hostPart, null);
            }

            if (!portPart.chars().allMatch(Character::isDigit)) {
                return new AllowedHost(trimmed, null);
            }

            try {
                int parsedPort = Integer.parseInt(portPart);
                return new AllowedHost(hostPart, parsedPort);
            } catch (NumberFormatException ex) {
                return new AllowedHost(hostPart, null);
            }
        }
    }
}
