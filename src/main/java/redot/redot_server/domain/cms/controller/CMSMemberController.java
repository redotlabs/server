package redot.redot_server.domain.cms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.dto.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.dto.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.dto.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.service.CMSMemberService;
import redot.redot_server.global.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/cms/members")
public class CMSMemberController {

    private final CMSMemberService cmsMemberService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping
    public ResponseEntity<CMSMemberResponse> createCMSMember(@CurrentCustomer Long customerId,
                                                             @RequestBody @Valid CMSMemberCreateRequest request) {
        return ResponseEntity.ok(cmsMemberService.createCMSMember(customerId, request));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<CMSMemberResponse> getCMSMemberInfo(@CurrentCustomer Long customerId,
                                                              @PathVariable(name = "memberId") Long memberId) {
        return ResponseEntity.ok(cmsMemberService.getCMSMemberInfo(customerId, memberId));
    }

    @GetMapping
    public ResponseEntity<List<CMSMemberResponse>> getCMSMemberList(@CurrentCustomer Long customerId) {
        return ResponseEntity.ok(cmsMemberService.getCMSMemberList(customerId));
    }

    @PatchMapping("/role/{memberId}")
    public ResponseEntity<CMSMemberResponse> changeCMSMemberRole(@CurrentCustomer Long customerId,
                                                                 @PathVariable(name = "memberId") Long memberId,
                                                                 @RequestBody @Valid CMSMemberRoleRequest request) {
        return ResponseEntity.ok(cmsMemberService.changeCMSMemberRole(customerId, memberId, request));
    }

    @PutMapping
    public ResponseEntity<CMSMemberResponse> updateCMSMember(@CurrentCustomer Long customerId,
                                                             @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
                                                             @RequestBody @Valid CMSMemberUpdateRequest request) {
        return ResponseEntity.ok(cmsMemberService.updateCMSMember(customerId, jwtPrincipal.id(), request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteCMSMember(@CurrentCustomer Long customerId,
                                                @PathVariable(name = "memberId") Long memberId) {
        cmsMemberService.deleteCMSMember(customerId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentCMSMember(HttpServletRequest request,
                                                       @CurrentCustomer Long customerId,
                                                       @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        cmsMemberService.deleteCMSMember(customerId, jwtPrincipal.id());
        ResponseCookie deleteAccess = tokenCookieFactory.deleteAccessTokenCookie(request,
                TokenType.CMS.getAccessCookieName());
        ResponseCookie deleteRefresh = tokenCookieFactory.deleteRefreshTokenCookie(request,
                TokenType.CMS.getRefreshCookieName());

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

}
