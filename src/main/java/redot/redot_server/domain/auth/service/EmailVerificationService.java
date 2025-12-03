package redot.redot_server.domain.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.auth.dto.response.EmailVerificationSendResponse;
import redot.redot_server.domain.auth.dto.response.EmailVerificationVerifyResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.global.email.EmailVerificationProperties;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailVerificationStore verificationStore;
    private final EmailVerificationMailService mailService;
    private final EmailVerificationProperties properties;

    public EmailVerificationSendResponse sendCode(EmailVerificationPurpose purpose, String email) {
        String normalizedEmail = normalize(email);

        if (properties.getResendCooldownSeconds() > 0 &&
                verificationStore.hasCooldown(purpose, normalizedEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_COOLDOWN);
        }

        String code = generateCode(properties.getCodeLength());
        Duration codeTtl = Duration.ofSeconds(properties.getCodeTtlSeconds());
        verificationStore.saveCode(purpose, normalizedEmail, code, codeTtl);

        if (properties.getResendCooldownSeconds() > 0) {
            verificationStore.saveCooldown(purpose, normalizedEmail,
                    Duration.ofSeconds(properties.getResendCooldownSeconds()));
        }

        mailService.sendVerificationMail(normalizedEmail, code, purpose, codeTtl);
        return new EmailVerificationSendResponse(
                properties.getCodeTtlSeconds(),
                properties.getResendCooldownSeconds()
        );
    }

    public EmailVerificationVerifyResponse verifyCode(EmailVerificationPurpose purpose,
                                                      String email,
                                                      String code) {
        String normalizedEmail = normalize(email);
        String trimmedCode = code == null ? null : code.trim();
        if (!StringUtils.hasText(trimmedCode)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }

        String storedCode = verificationStore.getCode(purpose, normalizedEmail)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_CODE));

        if (!Objects.equals(storedCode, trimmedCode)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }

        verificationStore.deleteCode(purpose, normalizedEmail);

        String token = UUID.randomUUID().toString();
        Duration tokenTtl = Duration.ofSeconds(properties.getTokenTtlSeconds());
        verificationStore.saveToken(purpose, token, normalizedEmail, tokenTtl);

        return new EmailVerificationVerifyResponse(token, properties.getTokenTtlSeconds());
    }

    public void consumeVerifiedToken(EmailVerificationPurpose purpose,
                                     String email,
                                     String token) {
        String normalizedEmail = normalize(email);
        if (!StringUtils.hasText(token)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        String storedEmail = verificationStore.getEmailByToken(purpose, token)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN));

        if (!storedEmail.equals(normalizedEmail)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        verificationStore.deleteToken(purpose, token);
    }

    private String normalize(String email) {
        String normalized = EmailUtils.normalize(email);
        if (!StringUtils.hasText(normalized)) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }
        return normalized;
    }

    private String generateCode(int length) {
        if (length <= 1) {
            length = 6;
        }
        int min = (int) Math.pow(10, length - 1);
        int bound = (int) Math.pow(10, length) - min;
        int number = SECURE_RANDOM.nextInt(bound) + min;
        return String.format(Locale.ROOT, "%0" + length + "d", number);
    }
}
