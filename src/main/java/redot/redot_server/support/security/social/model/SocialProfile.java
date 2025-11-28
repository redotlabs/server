package redot.redot_server.support.security.social.model;

public record SocialProfile(
        String email,
        String name,
        String profileImageUrl,
        String providerId
) {
}
