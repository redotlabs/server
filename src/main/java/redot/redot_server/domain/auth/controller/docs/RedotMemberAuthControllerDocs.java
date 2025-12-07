package redot.redot_server.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.RedotMemberSignInRequest;
import redot.redot_server.domain.auth.dto.response.SocialLoginUrlResponse;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.domain.redot.member.dto.request.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "Redot Member Auth", description = "Redot 회원 인증 API")
public interface RedotMemberAuthControllerDocs {

    @Operation(summary = "Redot 회원 회원가입", description = "새로운 Redot 회원 계정을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = RedotMemberResponse.class)))
    ResponseEntity<RedotMemberResponse> signUp(RedotMemberCreateRequest request);

    @Operation(summary = "Redot 회원 로그인", description = "Redot 회원 자격 증명을 사용해 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> signIn(@Parameter(hidden = true) HttpServletRequest request,
                                         RedotMemberSignInRequest signInRequest);

    @Operation(summary = "Redot 회원 토큰 재발급", description = "로그인된 회원의 토큰을 재발급합니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> reissue(@Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "현재 Redot 회원 정보 조회", description = "로그인된 회원 정보를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = RedotMemberResponse.class)))
    ResponseEntity<RedotMemberResponse> getCurrentMember(@Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "Redot 회원 로그아웃", description = "회원 토큰 쿠키를 삭제하여 로그아웃합니다.")
    @ApiResponse(responseCode = "204", description = "로그아웃 완료")
    ResponseEntity<Void> signOut(@Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "Redot 회원 비밀번호 재설정 확정", description = "비밀번호 재설정 토큰을 확인하고 비밀번호를 교체합니다.")
    @ApiResponse(responseCode = "204", description = "재설정 완료")
    ResponseEntity<Void> confirmPasswordReset(PasswordResetConfirmRequest request);

    @Operation(summary = "소셜 로그인 인가 URL 조회", description = "선택한 소셜 제공자의 OAuth2 인가 URL을 제공합니다.")
    @ApiResponse(responseCode = "200", description = "URL 생성 성공",
            content = @Content(schema = @Schema(implementation = SocialLoginUrlResponse.class)))
    ResponseEntity<SocialLoginUrlResponse> getSocialLoginUrl(
            @Parameter(description = "소셜 로그인 제공자", example = "google") String provider,
            @Parameter(description = "인가 완료 후 리다이렉트될 URI", example = "https://app.redot.com/auth/callback") String redirectUri,
            @Parameter(description = "실패 시 리다이렉트될 URI", example = "https://app.redot.com/auth/fail") String failureUri);
}
