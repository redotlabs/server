package redot.redot_server.domain.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    MISSING_ACCESS_TOKEN(401, 1000, "액세스 토큰이 필요합니다."),
    INVALID_TOKEN_TYPE(401, 1001, "유효하지 않은 토큰 타입입니다."),
    INVALID_TOKEN_SUBJECT(401, 1002, "유효하지 않은 사용자 정보입니다."),
    INVALID_USER_INFO(401, 1003, "유효하지 않은 사용자 정보입니다."),
    CUSTOMER_CONTEXT_REQUIRED(401, 1004, "도메인 기반 고객 정보를 파싱할 수 없습니다."),
    CUSTOMER_TOKEN_MISMATCH(401, 1005, "현재 고객 정보와 토큰의 고객 정보가 일치하지 않습니다."),
    TOKEN_EXPIRED(401, 1006, "만료된 토큰입니다."),
    INVALID_TOKEN(401, 1007, "유효하지 않은 토큰입니다."),
    EMPTY_TOKEN(401, 1008, "토큰 값이 비어 있습니다."),
    CUSTOMER_DOMAIN_NOT_FOUND(404, 1009, "고객 도메인을 찾을 수 없습니다."),
    CUSTOMER_INACTIVE(401, 1010, "활성화되지 않은 고객입니다."),
    MISSING_REFRESH_TOKEN(401, 1011, "리프레시 토큰이 필요합니다."),
    ADMIN_NOT_FOUND(404, 1012, "관리자 계정을 찾을 수 없습니다."),
    CMS_MEMBER_NOT_FOUND(404, 1013, "CMS 회원을 찾을 수 없습니다.")
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
