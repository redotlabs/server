package redot.redot_server.domain.cms.menu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.support.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CmsMenuErrorCode implements ErrorCode {
    CMS_MENU_NOT_FOUND(404, 5101, "CMS 메뉴를 찾을 수 없습니다"),
    CMS_MENU_PATH_DUPLICATED(409, 5102, "해당 플랜에 이미 존재하는 경로입니다"),
    CMS_MENU_ORDER_DUPLICATED(409, 5103, "해당 플랜에 이미 존재하는 순서입니다");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}

