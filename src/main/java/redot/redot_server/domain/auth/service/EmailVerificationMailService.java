package redot.redot_server.domain.auth.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.support.email.EmailVerificationProperties;

@Component
@RequiredArgsConstructor
public class EmailVerificationMailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationProperties properties;

    public void sendVerificationMail(String email,
                                     String code,
                                     EmailVerificationPurpose purpose,
                                     Duration expiresIn) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[%s] 이메일 인증 코드".formatted(properties.getBrandName()));
            message.setFrom(properties.getFromAddress());
            message.setText(buildBody(purpose, code, expiresIn));
            mailSender.send(message);
        } catch (MailException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED, ex);
        }
    }

    private String buildBody(EmailVerificationPurpose purpose, String code, Duration expiresIn) {
        long minutes = Math.max(1, expiresIn.toMinutes());
        return """
                %s 인증을 완료하려면 아래 코드를 입력해주세요.

                인증 코드: %s
                (유효 시간: %d분)

                본 메일은 발신 전용입니다.
                """.formatted(purpose.getDescription(), code, minutes);
    }
}
