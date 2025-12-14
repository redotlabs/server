package redot.redot_server.domain.cms.member.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.global.s3.util.ImageUrlResolver;

public record CMSMemberResponse(
        Long redotAppId,
        Long id,
        String name,
        String email,
        String profileImageUrl,
        CMSMemberRole role,
        LocalDateTime createdAt
) {
    public static CMSMemberResponse fromEntity(Long redotAppId, CMSMember cmsMember,
                                               ImageUrlResolver imageUrlResolver) {
        return new CMSMemberResponse(
                redotAppId,
                cmsMember.getId(),
                cmsMember.getName(),
                cmsMember.getEmail(),
                imageUrlResolver.toPublicUrl(cmsMember.getProfileImageUrl()),
                cmsMember.getRole(),
                cmsMember.getCreatedAt()
        );
    }
}
