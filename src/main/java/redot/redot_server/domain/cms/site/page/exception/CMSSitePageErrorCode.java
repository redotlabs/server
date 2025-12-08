package redot.redot_server.domain.cms.site.page.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CMSSitePageErrorCode implements ErrorCode {
    PAGE_NOT_FOUND(404, 3200, "페이지를 찾을 수 없습니다."),
    PAGE_NOT_BELONG_TO_APP(403, 3201, "요청한 페이지가 해당 앱에 속하지 않습니다."),
    INVALID_VERSION_STATUS(400, 3202, "허용되지 않은 버전 상태입니다."),
    RETAINED_PAGE_NOT_FOUND(404, 3203, "유지하려는 페이지를 찾을 수 없습니다."),
    PAGE_PATH_DUPLICATED(400, 3204, "해당 경로의 페이지가 이미 존재합니다."),
    PUBLISHED_VERSION_ALREADY_EXISTS(400, 3205, "이미 배포된 버전이 존재합니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
