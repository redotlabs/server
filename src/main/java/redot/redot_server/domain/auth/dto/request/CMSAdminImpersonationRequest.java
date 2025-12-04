package redot.redot_server.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record CMSAdminImpersonationRequest(
        @NotNull(message = "RedotApp ID를 입력해주세요.")
        Long redotAppId
) {
}
