package redot.redot_server.domain.admin.util;

import java.security.SecureRandom;

public class DomainNameGenerator {

    private static final int SUBDOMAIN_LENGTH = 8;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DOMAIN_SUFFIX = ".redot.me";

    public static String generateSubdomain() {
        StringBuilder domainName = new StringBuilder(SUBDOMAIN_LENGTH + DOMAIN_SUFFIX.length());
        for (int i = 0; i < SUBDOMAIN_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            domainName.append(CHARACTERS.charAt(index));
        }
        domainName.append(DOMAIN_SUFFIX);
        return domainName.toString();
    }
}
