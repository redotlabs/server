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
import redot.redot_server.domain.auth.service.EmailVerificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증 코드 발송")
    public ResponseEntity<EmailVerificationSendResponse> send(@RequestBody @Valid EmailVerificationSendRequest request) {
        EmailVerificationSendResponse response = emailVerificationService.sendCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증 코드 검증")
    public ResponseEntity<EmailVerificationVerifyResponse> verify(@RequestBody @Valid EmailVerificationVerifyRequest request) {
        EmailVerificationVerifyResponse response = emailVerificationService.verifyCode(request);
        return ResponseEntity.ok(response);
    }
}
