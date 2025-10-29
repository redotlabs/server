package redot.redot_server.domain.cms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import redot.redot_server.domain.cms.entity.Theme;

public record CustomerCreateRequest(
        @NotBlank(message = "회사 이름을 입력해주세요.")
        String companyName,
        Theme theme,
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String ownerEmail,
        String profileImageUrl,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @NotBlank(message = "이름을 입력해주세요.")
        String name
) {
    public CustomerCreateRequest{
        if (theme == null) {
            theme = Theme.CLASSIC;
        }
    }
}
