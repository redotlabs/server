package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import redot.redot_server.domain.auth.controller.docs.CMSAuthControllerDocs;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.domain.auth.service.CMSAuthService;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/app/cms")
public class CMSAuthController implements CMSAuthControllerDocs {

    private final CMSAuthService cmsAuthService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/sign-in")
    @Override
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request, @Valid @RequestBody SignInRequest signInRequest, @CurrentRedotApp Long redotAppId) {
        AuthResult authResult = cmsAuthService.signIn(request, signInRequest, redotAppId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/reissue")
    @Override
    public ResponseEntity<TokenResponse> reissueToken(@CurrentRedotApp Long redotAppId, HttpServletRequest request) {
        AuthResult authResult = cmsAuthService.reissueToken(redotAppId, request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<CMSMemberResponse> getCurrentCMSMemberInfo(@CurrentRedotApp Long redotAppId,
                                                                     @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        return ResponseEntity.ok(cmsAuthService.getCurrentCMSMemberInfo(redotAppId, jwtPrincipal.id()));
    }

    @PostMapping("/sign-out")
    @Override
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.CMS.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.CMS.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

    @PostMapping("/password-reset")
    @Override
    public ResponseEntity<Void> confirmPasswordReset(@CurrentRedotApp Long redotAppId,
                                                     @RequestBody @Valid PasswordResetConfirmRequest request) {
        cmsAuthService.resetPassword(redotAppId, request);
        return ResponseEntity.noContent().build();
    }

}
