package redot.redot_server.domain.redot.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.support.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum DomainErrorCode implements ErrorCode {
    RESERVED_DOMAIN_WITH_REDOT_APP(400, 2000, "예약 도메인에는 RedotApp이나 커스텀 도메인을 연결할 수 없습니다."),
    NON_RESERVED_DOMAIN_MISSING_REDOT_APP(400, 2001, "예약되지 않은 도메인에는 RedotApp이 반드시 연결되어야 합니다."),
    CUSTOM_DOMAIN_NOT_FOUND(404, 2002, "커스텀 도메인을 찾을 수 없습니다."),
    DOMAIN_NOT_FOUND(404, 2003, "도메인을 찾을 수 없습니다."),
    CUSTOM_DOMAIN_ALREADY_EXISTS(400, 2004, "이미 존재하는 커스텀 도메인입니다.");
    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
