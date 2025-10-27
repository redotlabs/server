package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.AdminDTO;
import redot.redot_server.domain.admin.service.AdminService;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.AdminAuthService;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final AdminService adminService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request, @RequestBody SignInRequest signInRequest) {
        AuthResult authResult = adminAuthService.signIn(request, signInRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AdminDTO> createAdmin(@RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @PostMapping("/reissue" )
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
        AuthResult authResult = adminAuthService.reissueToken(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @GetMapping("/me")
    public ResponseEntity<AdminDTO> getCurrentAdminInfo(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        AdminDTO adminDTO = adminAuthService.getCurrentAdminInfo(jwtPrincipal.id());
        return ResponseEntity.ok(adminDTO);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.ADMIN.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.ADMIN.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

}
