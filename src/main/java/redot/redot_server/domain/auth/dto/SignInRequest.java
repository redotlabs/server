package redot.redot_server.domain.auth.dto;

public record SignInRequest(
        String email,
        String password
) {
}
