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
    REDOT_APP_CONTEXT_REQUIRED(401, 1004, "도메인 기반 RedotApp 정보를 파싱할 수 없습니다."),
    REDOT_APP_TOKEN_MISMATCH(401, 1005, "현재 RedotApp 정보와 토큰의 RedotApp 정보가 일치하지 않습니다."),
    TOKEN_EXPIRED(401, 1006, "만료된 토큰입니다."),
    INVALID_TOKEN(401, 1007, "유효하지 않은 토큰입니다."),
    EMPTY_TOKEN(401, 1008, "토큰 값이 비어 있습니다."),
    REDOT_APP_DOMAIN_NOT_FOUND(404, 1009, "RedotApp 도메인을 찾을 수 없습니다."),
    REDOT_APP_INACTIVE(401, 1010, "활성화되지 않은 RedotApp입니다."),
    MISSING_REFRESH_TOKEN(401, 1011, "리프레시 토큰이 필요합니다."),
    ADMIN_NOT_FOUND(404, 1012, "관리자 계정을 찾을 수 없습니다."),
    CMS_MEMBER_NOT_FOUND(404, 1013, "CMS 회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(400, 1014, "이미 존재하는 이메일입니다."),
    CANNOT_DELETE_OWN_ADMIN_ACCOUNT(400, 1015, "자신의 관리자 계정은 삭제할 수 없습니다."),
    DELETED_USER(401, 1016, "삭제된 사용자입니다."),
    REDOT_MEMBER_NOT_FOUND(404, 1017, "Redot 회원을 찾을 수 없습니다."),
    UNSUPPORTED_SOCIAL_LOGIN_FLOW(400, 1018, "지원하지 않는 소셜 로그인 흐름입니다."),
    UNSUPPORTED_SOCIAL_LOGIN_PROVIDER(400, 1019, "지원하지 않는 소셜 로그인 제공자입니다."),
    INVALID_SOCIAL_LOGIN_REGISTRATION(400, 1020, "잘못된 소셜 로그인 registrationId 입니다."),
    INVALID_SOCIAL_PROFILE(400, 1021, "소셜 프로필 정보가 올바르지 않습니다."),
    EMAIL_SEND_FAILED(500, 1022, "이메일 발송에 실패했습니다."),
    EMAIL_VERIFICATION_COOLDOWN(429, 1023, "잠시 후에 다시 이메일 인증을 시도해주세요."),
    INVALID_EMAIL_VERIFICATION_CODE(400, 1024, "이메일 인증 코드가 올바르지 않습니다."),
    INVALID_EMAIL_VERIFICATION_TOKEN(400, 1025, "이메일 인증 토큰이 유효하지 않습니다."),
    UNSUPPORTED_EMAIL_VERIFICATION_PURPOSE(400, 1026, "지원하지 않는 이메일 인증 용도입니다."),
    EMAIL_NOT_VERIFIED(400, 1027, "이메일 인증이 필요합니다."),
    INVALID_MEMBER_STATUS_UPDATE(400, 1028, "지원하지 않는 회원 상태 변경입니다."),
    INVALID_MEMBER_STATUS_FILTER(400, 1029, "지원하지 않는 회원 상태 조회입니다."),
    BANNED_REDOT_MEMBER(403, 1030, "제한된 계정입니다.")
    ,
    SOCIAL_EMAIL_REQUIRED(400, 1031, "소셜 계정에서 이메일 정보를 제공해야 합니다.")
    ;


    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
