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
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.RedotMemberSignInRequest;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.service.RedotMemberAuthService;
import redot.redot_server.domain.redot.member.dto.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.support.jwt.cookie.TokenCookieFactory;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.support.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/redot/member")
public class RedotMemberAuthController {

    private final RedotMemberAuthService redotMemberAuthService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/sign-up")
    public ResponseEntity<RedotMemberResponse> signUp(@RequestBody RedotMemberCreateRequest request) {
        return ResponseEntity.ok(redotMemberAuthService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request,
                                                @RequestBody RedotMemberSignInRequest signInRequest) {
        AuthResult authResult = redotMemberAuthService.signIn(request, signInRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        AuthResult authResult = redotMemberAuthService.reissue(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @GetMapping("/me")
    public ResponseEntity<RedotMemberResponse> getCurrentMember(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        if (jwtPrincipal == null || jwtPrincipal.tokenType() != TokenType.REDOT_MEMBER) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }
        return ResponseEntity.ok(redotMemberAuthService.getCurrentMember(jwtPrincipal.id()));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.REDOT_MEMBER.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.REDOT_MEMBER.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }
}
