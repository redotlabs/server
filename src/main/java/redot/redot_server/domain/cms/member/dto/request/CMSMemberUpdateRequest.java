package redot.redot_server.domain.cms.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CMSMemberUpdateRequest(
        @NotBlank
        String name,
        String profileImageUrl
) {
}
