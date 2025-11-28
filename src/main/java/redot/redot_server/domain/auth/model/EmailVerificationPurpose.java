package redot.redot_server.domain.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Getter
@RequiredArgsConstructor
public enum EmailVerificationPurpose {
    REDOT_MEMBER_SIGN_UP("redot-member-sign-up", "Redot 회원가입 이메일 인증");

    private final String code;
    private final String description;

    public static EmailVerificationPurpose from(String value) {
        if (value == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_EMAIL_VERIFICATION_PURPOSE);
        }
        for (EmailVerificationPurpose purpose : values()) {
            if (purpose.code.equalsIgnoreCase(value) || purpose.name().equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new AuthException(AuthErrorCode.UNSUPPORTED_EMAIL_VERIFICATION_PURPOSE);
    }
}
