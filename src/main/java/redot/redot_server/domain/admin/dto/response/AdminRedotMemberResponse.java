package redot.redot_server.domain.admin.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberProjection;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.global.s3.util.ImageUrlResolver;

public record AdminRedotMemberResponse(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        SocialProvider socialProvider,
        LocalDateTime createdAt,
        RedotMemberStatus status,
        long appCount
) {

    public static AdminRedotMemberResponse fromProjection(AdminRedotMemberProjection projection,
                                                          ImageUrlResolver imageUrlResolver) {
        return new AdminRedotMemberResponse(
                projection.getId(),
                projection.getEmail(),
                projection.getName(),
                imageUrlResolver.toPublicUrl(projection.getProfileImageUrl()),
                projection.getSocialProvider(),
                projection.getCreatedAt(),
                projection.getStatus(),
                projection.getAppCount() == null ? 0L : projection.getAppCount()
        );
    }

    public static AdminRedotMemberResponse fromEntity(RedotMember member,
                                                      long appCount,
                                                      ImageUrlResolver imageUrlResolver) {
        return new AdminRedotMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                imageUrlResolver.toPublicUrl(member.getProfileImageUrl()),
                member.getSocialProvider(),
                member.getCreatedAt(),
                member.getStatus(),
                appCount
        );
    }
}
