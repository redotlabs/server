package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.controller.docs.AdminImpersonationControllerDocs;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.request.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.domain.auth.service.AdminImpersonationService;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/redot/admin/impersonation")
public class AdminImpersonationController implements AdminImpersonationControllerDocs {
    private final AdminImpersonationService adminImpersonationService;

    @PostMapping("/cms-admin")
    @Override
    public ResponseEntity<TokenResponse> impersonateAsCMSAdmin(HttpServletRequest request, @Valid @RequestBody CMSAdminImpersonationRequest cmsAdminImpersonationRequest) {
        AuthResult authResult = adminImpersonationService.impersonateAsCMSAdmin(request, cmsAdminImpersonationRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

}
