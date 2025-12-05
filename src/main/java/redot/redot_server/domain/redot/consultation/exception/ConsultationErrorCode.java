package redot.redot_server.domain.redot.consultation.exception;

import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
public enum ConsultationErrorCode implements ErrorCode {
    CONSULTATION_NOT_FOUND(404, 8000, "상담을 찾을 수 없습니다")
    ;
    private final int statusCode;
    private final int exceptionCode;
    private final String message;

    ConsultationErrorCode(int statusCode, int exceptionCode, String message) {
        this.statusCode = statusCode;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }
}
