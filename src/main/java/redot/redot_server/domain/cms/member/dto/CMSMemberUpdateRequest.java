package redot.redot_server.domain.cms.member.dto;

import jakarta.validation.constraints.NotBlank;

public record CMSMemberUpdateRequest(
        @NotBlank
        String name,
        String profileImageUrl
) {
}
