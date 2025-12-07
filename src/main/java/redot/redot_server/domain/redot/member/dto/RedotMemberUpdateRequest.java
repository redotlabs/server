package redot.redot_server.domain.redot.member.dto;

public record RedotMemberUpdateRequest(
        String name,
        String profileImageUrl
) {
}
