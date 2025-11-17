package redot.redot_server.support.exception;

import lombok.Getter;

@Getter
public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE(400, 9000, "잘못된 입력값입니다."),
    MISSING_REQUEST_PARAMETER(400, 9001, "필수 요청 값이 누락되었습니다."),
    MALFORMED_JSON_REQUEST(400, 9002, "요청 본문을 읽을 수 없습니다."),
    TYPE_MISMATCH(400, 9003, "요청 값의 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, 9004, "지원하지 않는 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND(404, 9005, "요청하신 경로를 찾을 수 없습니다."),
    MULTIPART_ERROR(400, 9006, "멀티파트 요청 처리에 실패했습니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;

    GlobalErrorCode(int statusCode, int exceptionCode, String message) {
        this.statusCode = statusCode;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }
}
