package redot.redot_server.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.AdminImpersonationService;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/admin/impersonation")
public class AdminImpersonationController {
    private final AdminImpersonationService adminImpersonationService;

    @PostMapping("/cms-admin")
    public ResponseEntity<TokenResponse> impersonateAsCMSAdmin(@RequestBody CMSAdminImpersonationRequest request, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        Long adminId = jwtPrincipal.id();

        AuthResult authResult = adminImpersonationService.impersonateAsCMSAdmin(request, adminId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

}
