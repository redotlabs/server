package redot.redot_server.domain.redot.member.dto;

import jakarta.validation.constraints.NotNull;

public record RedotMemberUpdateRequest(
        @NotNull
        String name,
        String profileImageUrl
) {
}
