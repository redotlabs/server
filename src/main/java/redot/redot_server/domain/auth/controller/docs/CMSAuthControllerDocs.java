package redot.redot_server.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "CMS Auth", description = "CMS 인증 API")
public interface CMSAuthControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "CMS 로그인", description = "CMS 계정으로 로그인하여 액세스/리프레시 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> signIn(@Parameter(hidden = true) HttpServletRequest request,
                                         @Valid SignInRequest signInRequest,
                                         @Parameter(hidden = true) Long redotAppId);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "CMS 토큰 재발급", description = "요청 쿠키에 포함된 리프레시 토큰으로 새 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> reissueToken(@Parameter(hidden = true) Long redotAppId,
                                               @Parameter(hidden = true) HttpServletRequest request);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "현재 CMS 멤버 정보 조회", description = "인증된 CMS 멤버의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CMSMemberResponse.class)))
    ResponseEntity<CMSMemberResponse> getCurrentCMSMemberInfo(@Parameter(hidden = true) Long redotAppId,
                                                              @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "CMS 로그아웃", description = "CMS 토큰 쿠키를 삭제하여 로그아웃합니다.")
    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    ResponseEntity<Void> signOut(@Parameter(hidden = true) HttpServletRequest request);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "CMS 비밀번호 재설정 확정", description = "발급받은 인증 코드로 비밀번호 재설정을 확정합니다.")
    @ApiResponse(responseCode = "204", description = "재설정 완료")
    ResponseEntity<Void> confirmPasswordReset(@Parameter(hidden = true) Long redotAppId,
                                              @Valid PasswordResetConfirmRequest request);
}
