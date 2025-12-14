package redot.redot_server.domain.redot.member.service;

import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;

@Component
public class RedotMemberStatusValidator {

    public void ensureActive(RedotMember redotMember) {
        if (redotMember.getStatus() == RedotMemberStatus.BANNED) {
            throw new AuthException(AuthErrorCode.BANNED_REDOT_MEMBER);
        }
    }
}
