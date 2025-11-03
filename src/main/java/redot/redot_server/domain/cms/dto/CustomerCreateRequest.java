package redot.redot_server.domain.cms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import redot.redot_server.domain.cms.entity.Theme;

public record CustomerCreateRequest(
        @NotBlank(message = "회사 이름을 입력해주세요.")
        String companyName,
        String ownerProfileImageUrl,
        Theme theme,
        @NotBlank(message = "색상을 입력해주세요.")
        String color,
        @NotBlank(message = "폰트를 입력해주세요.")
        String font,
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String ownerEmail,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String ownerPassword,
        @NotBlank(message = "이름을 입력해주세요.")
        String ownerName
        ) {
    public CustomerCreateRequest{
        if (theme == null) {
            theme = Theme.CLASSIC;
        }
    }
}
