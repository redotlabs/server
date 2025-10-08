package redot.redot_server.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.AdminDTO;
import redot.redot_server.domain.admin.service.AdminService;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.AdminAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final AdminService adminService;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request) {
        AuthResult authResult = adminAuthService.signIn(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AdminDTO> createAdmin(@RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

}
