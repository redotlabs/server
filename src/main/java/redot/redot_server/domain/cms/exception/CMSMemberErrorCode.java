package redot.redot_server.domain.cms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CMSMemberErrorCode implements ErrorCode {
    CMS_MEMBER_NOT_FOUND(404, 6000,"CMS 멤버를 찾을 수 없습니다.")
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
