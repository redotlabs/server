package redot.redot_server.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.dto.EmailVerificationSendRequest;
import redot.redot_server.domain.auth.dto.EmailVerificationSendResponse;
import redot.redot_server.domain.auth.dto.EmailVerificationVerifyRequest;
import redot.redot_server.domain.auth.dto.EmailVerificationVerifyResponse;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.domain.auth.service.EmailVerificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증 코드 발송")
    public ResponseEntity<EmailVerificationSendResponse> send(@RequestBody @Valid EmailVerificationSendRequest request) {
        EmailVerificationPurpose purpose = EmailVerificationPurpose.from(request.purpose());
        EmailVerificationSendResponse response = emailVerificationService.sendCode(purpose, request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증 코드 검증")
    public ResponseEntity<EmailVerificationVerifyResponse> verify(@RequestBody @Valid EmailVerificationVerifyRequest request) {
        EmailVerificationPurpose purpose = EmailVerificationPurpose.from(request.purpose());
        EmailVerificationVerifyResponse response = emailVerificationService.verifyCode(purpose, request.email(), request.code());
        return ResponseEntity.ok(response);
    }
}
