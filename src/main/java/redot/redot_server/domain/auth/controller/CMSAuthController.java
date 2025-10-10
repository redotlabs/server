package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.CMSAuthService;
import redot.redot_server.global.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/customer/cms")
public class CMSAuthController {

    private final CMSAuthService cmsAuthService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request, @CurrentCustomer Long customerId) {
        AuthResult authResult = cmsAuthService.signIn(request, customerId);
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

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(TokenType.CMS.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(TokenType.CMS.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

}
