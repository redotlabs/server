package redot.redot_server.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.dto.request.EmailVerificationSendRequest;
import redot.redot_server.domain.auth.dto.request.EmailVerificationVerifyRequest;
import redot.redot_server.domain.auth.dto.response.EmailVerificationSendResponse;
import redot.redot_server.domain.auth.dto.response.EmailVerificationVerifyResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.domain.auth.service.EmailVerificationService;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.redot.admin.repository.AdminRepository;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.util.EmailUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final RedotMemberRepository redotMemberRepository;
    private final AdminRepository adminRepository;
    private final CMSMemberRepository cmsMemberRepository;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증 코드 발송")
    public ResponseEntity<EmailVerificationSendResponse> send(@RequestBody @Valid EmailVerificationSendRequest request) {
        validatePurposeTarget(request);
        EmailVerificationSendResponse response = emailVerificationService.sendCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증 코드 검증")
    public ResponseEntity<EmailVerificationVerifyResponse> verify(@RequestBody @Valid EmailVerificationVerifyRequest request) {
        EmailVerificationVerifyResponse response = emailVerificationService.verifyCode(request);
        return ResponseEntity.ok(response);
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
                Long redotAppId = request.redotAppId();
                if (redotAppId == null) {
                    throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
                }
                cmsMemberRepository.findByEmailIgnoreCaseAndRedotApp_Id(normalizedEmail, redotAppId)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.CMS_MEMBER_NOT_FOUND));
            }
            default -> {
            }
        }
    }
}
