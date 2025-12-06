package redot.redot_server.domain.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.dto.request.EmailVerificationSendRequest;
import redot.redot_server.domain.auth.dto.request.EmailVerificationVerifyRequest;
import redot.redot_server.domain.auth.dto.response.EmailVerificationSendResponse;
import redot.redot_server.domain.auth.dto.response.EmailVerificationVerifyResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.global.email.EmailVerificationProperties;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailVerificationStore verificationStore;
    private final EmailVerificationMailService mailService;
    private final EmailVerificationProperties properties;
    private final RedotMemberRepository redotMemberRepository;
    private final AdminRepository adminRepository;
    private final CMSMemberRepository cmsMemberRepository;
    private final DomainRepository domainRepository;

    public EmailVerificationSendResponse sendCode(EmailVerificationSendRequest request) {
        validatePurposeTarget(request);

        String normalizedEmail = normalize(request.email());
        EmailVerificationPurpose purpose = request.purpose();

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

    private void validatePurposeTarget(EmailVerificationSendRequest request) {
        EmailVerificationPurpose purpose = request.purpose();
        if (purpose == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_EMAIL_VERIFICATION_PURPOSE);
        }
        String normalizedEmail = EmailUtils.normalize(request.email());
        switch (purpose) {
            case REDOT_MEMBER_PASSWORD_RESET -> redotMemberRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
            case REDOT_ADMIN_PASSWORD_RESET -> adminRepository.findByEmailIgnoreCase(normalizedEmail)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));
            case CMS_MEMBER_PASSWORD_RESET -> {
                String redotAppSubdomain = request.redotAppSubdomain();
                if (!StringUtils.hasText(redotAppSubdomain)) {
                    throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
                }

                Long redotAppId = domainRepository.findBySubdomain(redotAppSubdomain).orElseThrow(() ->
                        new AuthException(AuthErrorCode.REDOT_APP_DOMAIN_NOT_FOUND)
                ).getRedotApp().getId();

                cmsMemberRepository.findByEmailIgnoreCaseAndRedotApp_Id(normalizedEmail, redotAppId)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.CMS_MEMBER_NOT_FOUND));
            }
            default -> {
            }
        }
    }

    public EmailVerificationVerifyResponse verifyCode(EmailVerificationVerifyRequest request) {
        String normalizedEmail = normalize(request.email());
        EmailVerificationPurpose purpose = request.purpose();
        String trimmedCode = request.code() == null ? null : request.code().trim();

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
