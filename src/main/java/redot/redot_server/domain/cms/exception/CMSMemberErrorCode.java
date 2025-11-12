package redot.redot_server.domain.cms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CMSMemberErrorCode implements ErrorCode {
    CMS_MEMBER_NOT_FOUND(404, 6000, "CMS 멤버를 찾을 수 없습니다."),
    CMS_MEMBER_ROLE_UNCHANGED(400, 6001, "변경할 CMS 멤버의 역할이 기존과 동일합니다."),
    CMS_MEMBER_ALREADY_DELETED(400, 6002, "이미 삭제된 CMS 멤버입니다.")
    ;

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
