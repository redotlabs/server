package redot.redot_server.domain.cms.member.exception;

import redot.redot_server.global.exception.BaseException;

public class CMSMemberException extends BaseException {
    public CMSMemberException(CMSMemberErrorCode errorCode) {
        super(errorCode);
    }
}
