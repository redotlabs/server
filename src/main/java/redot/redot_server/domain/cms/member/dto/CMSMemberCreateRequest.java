package redot.redot_server.domain.cms.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;

public record CMSMemberCreateRequest(
        @NotBlank
        String name,
        @Email
        @NotNull
        String email,
        @NotBlank
        String password,
        @NotNull
        CMSMemberRole role
) {
}
