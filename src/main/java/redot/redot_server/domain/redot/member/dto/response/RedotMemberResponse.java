package redot.redot_server.domain.redot.member.dto.response;

import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.global.s3.util.ImageUrlResolver;

public record RedotMemberResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        SocialProvider socialProvider
) {
    public static RedotMemberResponse fromEntity(RedotMember member, ImageUrlResolver imageUrlResolver) {
        return new RedotMemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                imageUrlResolver.toPublicUrl(member.getProfileImageUrl()),
                member.getSocialProvider()
        );
    }

    public static RedotMemberResponse fromNullable(RedotMember member, ImageUrlResolver imageUrlResolver) {
        return member == null ? null : fromEntity(member, imageUrlResolver);
    }
}
