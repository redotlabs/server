package redot.redot_server.domain.cms.member.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;

public record CMSMemberResponse(
        Long customerId,
        Long id,
        String name,
        String email,
        String profileImageUrl,
        CMSMemberRole role,
        LocalDateTime createdAt
) {
    public static CMSMemberResponse fromEntity(Long customerId, CMSMember cmsMember) {
        return new CMSMemberResponse(
                customerId,
                cmsMember.getId(),
                cmsMember.getName(),
                cmsMember.getEmail(),
                cmsMember.getProfileImageUrl(),
                cmsMember.getRole(),
                cmsMember.getCreatedAt()
        );
    }
}
