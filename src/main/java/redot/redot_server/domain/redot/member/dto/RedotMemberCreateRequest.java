package redot.redot_server.domain.redot.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RedotMemberCreateRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @NotBlank(message = "이름을 입력해주세요.")
        String name
) {
}
