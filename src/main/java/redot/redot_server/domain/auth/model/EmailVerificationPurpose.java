package redot.redot_server.domain.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailVerificationPurpose {
    REDOT_MEMBER_SIGN_UP("Redot 회원가입 이메일 인증"),
    REDOT_MEMBER_PASSWORD_RESET("Redot 회원 비밀번호 초기화"),
    REDOT_ADMIN_PASSWORD_RESET("Admin 비밀번호 초기화"),
    CMS_MEMBER_PASSWORD_RESET("CMS 멤버 비밀번호 초기화");

    private final String description;
}
