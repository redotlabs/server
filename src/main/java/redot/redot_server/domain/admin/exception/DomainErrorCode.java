package redot.redot_server.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum DomainErrorCode implements ErrorCode {
    RESERVED_DOMAIN_WITH_CUSTOMER(400, 2000, "예약 도메인에는 고객이나 커스텀 도메인을 연결할 수 없습니다."),
    NON_RESERVED_DOMAIN_MISSING_CUSTOMER(400, 2001, "예약되지 않은 도메인에는 고객이 반드시 연결되어야 합니다."),
    CUSTOM_DOMAIN_NOT_FOUND(404, 2002, "커스텀 도메인을 찾을 수 없습니다."),
    DOMAIN_NOT_FOUND(404, 2003, "도메인을 찾을 수 없습니다."),
    CUSTOM_DOMAIN_ALREADY_EXISTS(400, 2004, "이미 존재하는 커스텀 도메인입니다.");
    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
