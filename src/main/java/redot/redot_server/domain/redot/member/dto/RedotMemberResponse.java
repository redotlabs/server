package redot.redot_server.domain.redot.member.dto;

import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public record RedotMemberResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        SocialProvider socialProvider
) {
    public static RedotMemberResponse from(RedotMember member) {
        return new RedotMemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getProfileImageUrl(),
                member.getSocialProvider()
        );
    }

    public static RedotMemberResponse fromNullable(RedotMember member) {
        return member == null ? null : from(member);
    }
}
