package redot.redot_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import redot.redot_server.domain.auth.controller.docs.RedotMemberAuthControllerDocs;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.RedotMemberSignInRequest;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.response.SocialLoginUrlResponse;
import redot.redot_server.domain.auth.dto.response.TokenResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.service.RedotMemberAuthService;
import redot.redot_server.domain.redot.member.dto.request.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;
import redot.redot_server.global.security.social.SocialAuthorizationEndpoints;
import redot.redot_server.global.security.social.config.AuthRedirectProperties;
import redot.redot_server.global.security.social.config.FlowRedirect;

@RestController
@RequestMapping("/api/v1/auth/redot/member")
public class RedotMemberAuthController implements RedotMemberAuthControllerDocs {

    private final RedotMemberAuthService redotMemberAuthService;
    private final TokenCookieFactory tokenCookieFactory;
    private final String oauth2BaseUrl;
    private final AuthRedirectProperties authRedirectProperties;

    public RedotMemberAuthController(RedotMemberAuthService redotMemberAuthService,
                                     TokenCookieFactory tokenCookieFactory,
                                     @Value("${spring.security.oauth2.base-url}") String oauth2BaseUrl,
                                     AuthRedirectProperties authRedirectProperties) {
        this.redotMemberAuthService = redotMemberAuthService;
        this.tokenCookieFactory = tokenCookieFactory;
        this.oauth2BaseUrl = oauth2BaseUrl;
        this.authRedirectProperties = authRedirectProperties;
    }

    @PostMapping("/sign-up")
    @Override
    public ResponseEntity<RedotMemberResponse> signUp(@RequestBody @Valid RedotMemberCreateRequest request) {
        return ResponseEntity.ok(redotMemberAuthService.signUp(request));
    }

    @PostMapping("/sign-in")
    @Override
    public ResponseEntity<TokenResponse> signIn(HttpServletRequest request,
                                                @RequestBody @Valid RedotMemberSignInRequest signInRequest) {
        AuthResult authResult = redotMemberAuthService.signIn(request, signInRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @PostMapping("/reissue")
    @Override
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        AuthResult authResult = redotMemberAuthService.reissue(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<RedotMemberResponse> getCurrentMember(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        if (jwtPrincipal == null || jwtPrincipal.tokenType() != TokenType.REDOT_MEMBER) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }
        return ResponseEntity.ok(redotMemberAuthService.getCurrentMember(jwtPrincipal.id()));
    }

    @PostMapping("/sign-out")
    @Override
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request, TokenType.REDOT_MEMBER.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request, TokenType.REDOT_MEMBER.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

    @PostMapping("/password-reset")
    @Override
    public ResponseEntity<Void> confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmRequest request) {
        redotMemberAuthService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/social/login-url")
    @Override
    public ResponseEntity<SocialLoginUrlResponse> getSocialLoginUrl(@RequestParam(name = "provider", defaultValue = "google") String provider,
                                                                    @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                                                                    @RequestParam(name = "failure_uri", required = false) String failureUri) {
        String normalizedProvider = provider.toLowerCase();
        String registrationId = "redot-member-" + normalizedProvider;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(resolveAuthorizationBaseUrl("redot-member", redirectUri))
                .path(SocialAuthorizationEndpoints.API_AUTHORIZATION_BASE_URI)
                .path("/")
                .path(registrationId);

        if (StringUtils.hasText(redirectUri)) {
            builder.queryParam("redirect_uri", redirectUri);
        }
        if (StringUtils.hasText(failureUri)) {
            builder.queryParam("failure_uri", failureUri);
        }

        return ResponseEntity.ok(new SocialLoginUrlResponse(builder.toUriString()));
    }

    private String resolveAuthorizationBaseUrl(String flowKey, String requestedRedirect) {
        if (!StringUtils.hasText(requestedRedirect)) {
            return oauth2BaseUrl;
        }

        FlowRedirect flowRedirect;
        try {
            flowRedirect = authRedirectProperties.getFlow(flowKey);
        } catch (AuthException ex) {
            return oauth2BaseUrl;
        }

        if (!flowRedirect.isAllowed(requestedRedirect)) {
            return oauth2BaseUrl;
        }

        try {
            URI parsed = URI.create(requestedRedirect);
            if (!StringUtils.hasText(parsed.getScheme()) || !StringUtils.hasText(parsed.getHost())) {
                return oauth2BaseUrl;
            }

            UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                    .scheme(parsed.getScheme())
                    .host(parsed.getHost());
            if (parsed.getPort() >= 0) {
                builder.port(parsed.getPort());
            }
            return builder.build(true).toUriString();
        } catch (IllegalArgumentException ex) {
            return oauth2BaseUrl;
        }
    }
}
