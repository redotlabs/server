package redot.redot_server.global.customer.util;

import java.util.Locale;
import java.util.Optional;
import org.springframework.util.StringUtils;

public final class DomainParser {

    private DomainParser() {
    }

    public static Optional<String> extractDomain(String hostHeader) {
        if (!StringUtils.hasText(hostHeader)) {
            return Optional.empty();
        }

        String normalized = hostHeader.trim().toLowerCase(Locale.ROOT);
        int portSeparator = normalized.indexOf(':');
        if (portSeparator >= 0) {
            normalized = normalized.substring(0, portSeparator);
        }

        if (!StringUtils.hasText(normalized)) {
            return Optional.empty();
        }

        return Optional.of(normalized);
    }
}
