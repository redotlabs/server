package redot.redot_server.domain.cms.plan.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum PlanErrorCode implements ErrorCode {
    PLAN_NOT_FOUND(404, 5001, "플랜을 찾을 수 없습니다");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}

