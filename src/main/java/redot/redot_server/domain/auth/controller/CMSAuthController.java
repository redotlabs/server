package redot.redot_server.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.TokenResponse;
import redot.redot_server.domain.auth.service.CMSAuthService;
import redot.redot_server.global.customer.resolver.annotation.CurrentCustomer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/customer/cms")
public class CMSAuthController {

    private final CMSAuthService cmsAuthService;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request, @CurrentCustomer Long customerId) {
        AuthResult authResult = cmsAuthService.signIn(request, customerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.accessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, authResult.refreshCookie().toString())
                .body(authResult.tokenResponse());
    }

}
