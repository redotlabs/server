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
    NOT_REDOT_APP_OWNER(403, 3003, "RedotApp 소유자가 아닙니다."),
    MANAGER_ALREADY_CREATED(400, 3004, "이미 초기 관리자 계정이 생성되었습니다."),
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
