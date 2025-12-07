package redot.redot_server.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.admin.dto.request.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.response.AdminResponse;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "Admin Auth", description = "Redot 관리자 인증 API")
public interface AdminAuthControllerDocs {

    @Operation(summary = "관리자 로그인", description = "Redot 관리자 계정으로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> signIn(@Parameter(hidden = true) HttpServletRequest request,
                                         SignInRequest signInRequest);

    @Operation(summary = "관리자 회원가입", description = "새로운 Redot 관리자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = AdminResponse.class)))
    ResponseEntity<AdminResponse> createAdmin(AdminCreateRequest request);

    @Operation(summary = "관리자 토큰 재발급", description = "만료 직전의 토큰을 쿠키 기반으로 재발급합니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> refreshToken(@Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "현재 관리자 정보 조회", description = "로그인된 관리자 정보를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AdminResponse.class)))
    ResponseEntity<AdminResponse> getCurrentAdminInfo(@Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "관리자 로그아웃", description = "관리자 토큰 쿠키를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "로그아웃 완료")
    ResponseEntity<Void> signOut(@Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "관리자 비밀번호 재설정 확정", description = "비밀번호 재설정 코드를 확인하고 새 비밀번호를 저장합니다.")
    @ApiResponse(responseCode = "204", description = "재설정 완료")
    ResponseEntity<Void> confirmPasswordReset(PasswordResetConfirmRequest request);
}
