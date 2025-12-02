package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.RedotMemberSignInRequest;
import redot.redot_server.domain.auth.dto.SocialLoginUrlResponse;
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
@RequestMapping("/api/v1/auth/redot/member")
public class RedotMemberAuthController {

    private final RedotMemberAuthService redotMemberAuthService;
    private final TokenCookieFactory tokenCookieFactory;
    private final String oauth2BaseUrl;

    public RedotMemberAuthController(RedotMemberAuthService redotMemberAuthService,
                                     TokenCookieFactory tokenCookieFactory,
                                     @Value("${spring.security.oauth2.base-url}") String oauth2BaseUrl) {
        this.redotMemberAuthService = redotMemberAuthService;
        this.tokenCookieFactory = tokenCookieFactory;
        this.oauth2BaseUrl = oauth2BaseUrl;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<RedotMemberResponse> signUp(@RequestBody @Valid RedotMemberCreateRequest request) {
        return ResponseEntity.ok(redotMemberAuthService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request,
                                                @RequestBody @Valid RedotMemberSignInRequest signInRequest) {
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

    @GetMapping("/social/login-url")
    @Operation(summary = "소셜 로그인 인가 URL 조회",
            description = "프론트엔드가 브라우저를 리다이렉트하기 위해 OAuth2 인가 URL을 받아갈 때 사용합니다.")
    public ResponseEntity<SocialLoginUrlResponse> getSocialLoginUrl(@RequestParam(name = "provider", defaultValue = "google") String provider,
                                                                    @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                                                                    @RequestParam(name = "failure_uri", required = false) String failureUri) {
        String normalizedProvider = provider.toLowerCase();
        String registrationId = "redot-member-" + normalizedProvider;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(oauth2BaseUrl)
                .path("/oauth2/authorization/")
                .path(registrationId);

        if (StringUtils.hasText(redirectUri)) {
            builder.queryParam("redirect_uri", redirectUri);
        }
        if (StringUtils.hasText(failureUri)) {
            builder.queryParam("failure_uri", failureUri);
        }

        return ResponseEntity.ok(new SocialLoginUrlResponse(builder.toUriString()));
    }
}
