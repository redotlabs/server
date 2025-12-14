package redot.redot_server.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;

public record AdminRedotMemberUpdateRequest(
        @NotBlank
        String name,
        String profileImageUrl,
        @NotNull
        RedotMemberStatus status
) {
}
