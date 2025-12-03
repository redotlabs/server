package redot.redot_server.domain.cms.style.dto.response;

import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.site.entity.Theme;

public record StyleInfoResponse(
        String color,
        String font,
        Theme theme
) {
    public static StyleInfoResponse fromEntity(StyleInfo styleInfo) {
        return new StyleInfoResponse(
                styleInfo.getColor(),
                styleInfo.getFont(),
                styleInfo.getTheme()
        );
    }
}
