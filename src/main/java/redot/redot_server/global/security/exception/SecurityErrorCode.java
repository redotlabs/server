package redot.redot_server.global.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
    UNAUTHORIZED(401, "Unauthorized", "인증이 필요합니다."),
    FORBIDDEN(403, "Forbidden", "접근 권한이 없습니다.");

    private final int status;
    private final String error;
    private final String message;
}
