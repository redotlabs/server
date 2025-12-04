package redot.redot_server.domain.cms.plan.exception;

import lombok.Getter;
import redot.redot_server.global.exception.BaseException;

@Getter
public class PlanException extends BaseException {
    private final PlanErrorCode errorCode;

    public PlanException(PlanErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

