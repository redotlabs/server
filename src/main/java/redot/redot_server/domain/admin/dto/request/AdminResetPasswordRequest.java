package redot.redot_server.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminResetPasswordRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
