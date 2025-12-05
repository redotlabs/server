package redot.redot_server.domain.redot.consultation.exception;

import redot.redot_server.global.exception.BaseException;

public class ConsultationException extends BaseException {

    public ConsultationException(ConsultationErrorCode errorCode) {
        super(errorCode);
    }
}
