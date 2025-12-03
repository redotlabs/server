package redot.redot_server.global.security.social.model;

public record SocialProfile(
        String email,
        String name,
        String profileImageUrl,
        String providerId
) {
}
