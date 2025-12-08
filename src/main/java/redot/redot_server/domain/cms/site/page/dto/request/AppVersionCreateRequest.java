package redot.redot_server.domain.cms.site.page.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;

public record AppVersionCreateRequest(
        @NotNull AppVersionStatus status,
        String remark,
        @Valid List<AppPageRetainRequest> retained,
        @Valid List<AppPageCreateRequest> added
) {
}
