package redot.redot_server.domain.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.cms.entity.CMSMemberRole;
import redot.redot_server.domain.cms.entity.Theme;

public record CustomerCreateRequest(
        @NotBlank
        String companyName,
        Theme theme,
        @NotBlank
        String ownerEmail,
        String profileImageUrl,
        @NotBlank
        String password,
        @NotBlank
        String name,
        @NotNull
        CMSMemberRole role
) {
    public CustomerCreateRequest{
        if (theme == null) {
            theme = Theme.CLASSIC;
        }
    }
}
