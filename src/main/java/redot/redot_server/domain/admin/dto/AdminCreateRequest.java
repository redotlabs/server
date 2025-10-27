package redot.redot_server.domain.admin.dto;

public record AdminCreateRequest(
        String name,
        String email,
        String profileImageUrl,
        String password
) {
}
