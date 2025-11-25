package redot.redot_server.domain.redot.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.redot.admin.dto.AdminResetPasswordRequest;
import redot.redot_server.domain.redot.admin.dto.AdminResponse;
import redot.redot_server.domain.redot.admin.dto.AdminUpdateRequest;
import redot.redot_server.domain.redot.admin.service.AdminService;
import redot.redot_server.support.common.dto.PageResponse;
import redot.redot_server.support.jwt.cookie.TokenCookieFactory;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.support.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final TokenCookieFactory tokenCookieFactory;

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponse> getAdminInfo(@PathVariable("adminId") Long adminId) {
        return ResponseEntity.ok(adminService.getAdminInfo(adminId));
    }

    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdminResponse>> getAdminInfoList(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAdminInfoList(pageable));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<AdminResponse> updateAdmin(@PathVariable("adminId") Long adminId, @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(adminId, request));
    }

    @PostMapping("/{adminId}/reset-password")
    public ResponseEntity<Void> resetAdminPassword(@PathVariable("adminId") Long adminId, @Valid @RequestBody AdminResetPasswordRequest request) {
        adminService.resetAdminPassword(adminId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable("adminId") Long adminId, @AuthenticationPrincipal
    JwtPrincipal jwtPrincipal) {
        adminService.deleteAdmin(jwtPrincipal.id(), adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentAdmin(HttpServletRequest request, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        adminService.deleteCurrentAdmin(jwtPrincipal.id());
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.ADMIN.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.ADMIN.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }
}
