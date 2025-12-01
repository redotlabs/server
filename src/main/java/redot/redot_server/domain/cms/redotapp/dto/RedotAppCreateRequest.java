package redot.redot_server.domain.cms.redotapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.cms.site.entity.Theme;

public record RedotAppCreateRequest(
        @NotBlank(message = "앱 이름을 입력해주세요.")
        String name,
        String ownerProfileImageUrl,
        Theme theme,
        @NotBlank(message = "색상을 입력해주세요.")
        String color,
        @NotBlank(message = "폰트를 입력해주세요.")
        String font,
        @NotNull(message = "플랜을 선택해주세요.")
        Long planId
        ) {
    public RedotAppCreateRequest{
        if (theme == null) {
            theme = Theme.DEFAULT;
        }
    }
}
