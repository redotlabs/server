package redot.redot_server.domain.admin.dto;

import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public record AdminRedotMemberSearchCondition(
        String email,
        String name,
        SocialProvider socialProvider,
        RedotMemberStatus status
) {
}
