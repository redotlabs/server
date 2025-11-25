package redot.redot_server.domain.cms.redotapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.support.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum RedotAppErrorCode implements ErrorCode {
    OWNER_ALREADY_ASSIGNED(400, 3000, "이미 소유자가 지정된 RedotApp입니다."),
    REDOT_APP_NOT_FOUND(404, 3001, "RedotApp을 찾을 수 없습니다."),
    REDOT_APP_OWNER_NOT_FOUND(404, 3002, "RedotApp의 소유자를 찾을 수 없습니다."),
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
