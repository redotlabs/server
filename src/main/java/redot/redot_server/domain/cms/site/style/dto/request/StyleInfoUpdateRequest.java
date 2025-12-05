package redot.redot_server.domain.cms.site.style.dto.request;

import redot.redot_server.domain.site.setting.entity.Theme;

public record StyleInfoUpdateRequest(
        String color,
        String font,
        Theme theme
) {
}
