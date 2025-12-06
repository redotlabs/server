package redot.redot_server.domain.site.domain.util;

import java.security.SecureRandom;

public class SubDomainNameGenerator {

    private static final int SUBDOMAIN_LENGTH = 8;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateSubdomain() {
        StringBuilder domainName = new StringBuilder(SUBDOMAIN_LENGTH);
        for (int i = 0; i < SUBDOMAIN_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            domainName.append(CHARACTERS.charAt(index));
        }
        return domainName.toString();
    }
}
