package redot.redot_server.global.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
    UNAUTHORIZED(401, 10000, "인증이 필요합니다."),
    FORBIDDEN(403, 10001, "접근 권한이 없습니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
