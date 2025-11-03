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
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.CMSAuthService;
import redot.redot_server.domain.cms.dto.CMSMemberResponse;
import redot.redot_server.global.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/customer/cms")
public class CMSAuthController {

    private final CMSAuthService cmsAuthService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request, @Valid @RequestBody SignInRequest signInRequest, @CurrentCustomer Long customerId) {
        AuthResult authResult = cmsAuthService.signIn(request, signInRequest, customerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueToken(@CurrentCustomer Long customerId, HttpServletRequest request) {
        AuthResult authResult = cmsAuthService.reissueToken(customerId, request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @GetMapping("/me")
    public ResponseEntity<CMSMemberResponse> getCurrentCMSMemberInfo(@CurrentCustomer Long customerId, @AuthenticationPrincipal
                                                 JwtPrincipal jwtPrincipal) {
        return ResponseEntity.ok(cmsAuthService.getCurrentCMSMemberInfo(customerId, jwtPrincipal.id()));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.CMS.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.CMS.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

}
