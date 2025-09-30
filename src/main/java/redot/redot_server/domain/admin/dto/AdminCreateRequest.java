package redot.redot_server.domain.admin.dto;

public record AdminCreateRequest(
        String email,
        String password
) {
}
