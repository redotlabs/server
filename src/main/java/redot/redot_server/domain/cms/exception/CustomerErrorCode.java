package redot.redot_server.domain.cms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CustomerErrorCode implements ErrorCode {
    OWNER_ALREADY_ASSIGNED(400, 3000, "이미 소유자가 지정된 고객입니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
