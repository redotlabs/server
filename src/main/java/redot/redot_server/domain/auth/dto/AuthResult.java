package redot.redot_server.domain.auth.dto;

import org.springframework.http.ResponseCookie;

public record AuthResult(
        ResponseCookie accessCookie,
        ResponseCookie refreshCookie,
        TokenResponse tokenResponse
) {
}
