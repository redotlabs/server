package redot.redot_server.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.auth.dto.request.EmailVerificationSendRequest;
import redot.redot_server.domain.auth.dto.request.EmailVerificationVerifyRequest;
import redot.redot_server.domain.auth.dto.response.EmailVerificationSendResponse;
import redot.redot_server.domain.auth.dto.response.EmailVerificationVerifyResponse;

@Tag(name = "Email Verification", description = "이메일 인증 API")
public interface EmailVerificationControllerDocs {

    @Operation(summary = "이메일 인증 코드 발송", description = "입력한 이메일로 인증 코드를 발송합니다.")
    @ApiResponse(responseCode = "200", description = "발송 성공",
            content = @Content(schema = @Schema(implementation = EmailVerificationSendResponse.class)))
    ResponseEntity<EmailVerificationSendResponse> send(@Valid EmailVerificationSendRequest request);

    @Operation(summary = "이메일 인증 코드 검증", description = "발송된 인증 코드가 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "검증 성공",
            content = @Content(schema = @Schema(implementation = EmailVerificationVerifyResponse.class)))
    ResponseEntity<EmailVerificationVerifyResponse> verify(@Valid EmailVerificationVerifyRequest request);
}
