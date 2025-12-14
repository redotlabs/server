package redot.redot_server.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.auth.dto.request.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "Admin Impersonation", description = "관리자 권한 위임 API")
public interface AdminImpersonationControllerDocs {

    @Operation(summary = "CMS 관리자 사칭 토큰 발급", description = "최상위 관리자가 CMS 관리자 권한으로 임시 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    ResponseEntity<TokenResponse> impersonateAsCMSAdmin(@Parameter(hidden = true) HttpServletRequest request,
                                                        @Valid CMSAdminImpersonationRequest cmsAdminImpersonationRequest,
                                                        @Parameter(hidden = true) JwtPrincipal jwtPrincipal);
}
