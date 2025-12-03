package redot.redot_server.domain.cms.style.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StyleInfoErrorCode implements ErrorCode {
    STYLE_INFO_NOT_FOUND(404, 3200, "스타일 정보를 찾을 수 없습니다."),
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
