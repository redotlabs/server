package redot.redot_server.domain.site.page.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum SitePageErrorCode implements ErrorCode {
    PUBLISHED_VERSION_NOT_FOUND(404, 3100, "배포된 페이지 버전을 찾을 수 없습니다."),
    PAGE_NOT_FOUND(404, 3101, "요청한 경로의 페이지를 찾을 수 없습니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
