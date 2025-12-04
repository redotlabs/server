package redot.redot_server.domain.auth.dto.response;

import org.springframework.http.ResponseCookie;

public record AuthResult(
        ResponseCookie accessCookie,
        ResponseCookie refreshCookie,
        TokenResponse tokenResponse
) {
}
