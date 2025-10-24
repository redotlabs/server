package redot.redot_server.domain.cms.exception;

import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
public enum SiteSettingErrorCode implements ErrorCode {
    SITE_SETTING_NOT_FOUND(404, 4000, "사이트 설정 정보를 찾을 수 없습니다."),
    LOGO_FILE_REQUIRED(400, 4001, "업로드할 로고 파일이 필요합니다."),
    LOGO_FILE_NAME_REQUIRED(400, 4002, "로고 파일 이름이 필요합니다."),
    LOGO_FILE_EXTENSION_REQUIRED(400, 4003, "로고 파일 확장자를 확인할 수 없습니다."),
    INVALID_FILE_EXTENSION_FORMAT(400, 4004, "지원하지 않는 파일 확장자입니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;

    SiteSettingErrorCode(int statusCode, int exceptionCode, String message) {
        this.statusCode = statusCode;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }
}
