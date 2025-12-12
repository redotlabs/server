package redot.redot_server.domain.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.admin.controller.docs.AdminControllerDocs;
import redot.redot_server.domain.admin.dto.request.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.request.AdminResetPasswordRequest;
import redot.redot_server.domain.admin.dto.response.AdminResponse;
import redot.redot_server.domain.admin.dto.request.AdminUpdateRequest;
import redot.redot_server.domain.admin.service.AdminService;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.util.dto.response.PageResponse;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redot/admin")
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;
    private final TokenCookieFactory tokenCookieFactory;

    @GetMapping("/{adminId}")
    @Override
    public ResponseEntity<AdminResponse> getAdminInfo(@PathVariable("adminId") Long adminId) {
        return ResponseEntity.ok(adminService.getAdminInfo(adminId));
    }

    @PostMapping
    @Override
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<AdminResponse>> getAdminInfoList(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAdminInfoList(pageable));
    }

    @PutMapping("/{adminId}")
    @Override
    public ResponseEntity<AdminResponse> updateAdmin(@PathVariable("adminId") Long adminId, @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(adminId, request));
    }

    @PostMapping("/{adminId}/reset-password")
    @Override
    public ResponseEntity<Void> resetAdminPassword(@PathVariable("adminId") Long adminId, @Valid @RequestBody AdminResetPasswordRequest request) {
        adminService.resetAdminPassword(adminId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}")
    @Override
    public ResponseEntity<Void> deleteAdmin(@PathVariable("adminId") Long adminId, @AuthenticationPrincipal
    JwtPrincipal jwtPrincipal) {
        adminService.deleteAdmin(jwtPrincipal.id(), adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteCurrentAdmin(HttpServletRequest request, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        adminService.deleteCurrentAdmin(jwtPrincipal.id());
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.ADMIN.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.ADMIN.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
            @RequestPart("image") @NotNull MultipartFile image
    ) {
        return ResponseEntity.ok(adminService.uploadProfileImage(jwtPrincipal.id(), image));
    }
}
