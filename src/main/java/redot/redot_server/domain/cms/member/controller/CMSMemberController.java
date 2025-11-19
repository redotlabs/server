package redot.redot_server.domain.cms.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import redot.redot_server.domain.cms.member.dto.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.member.dto.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.member.service.CMSMemberService;
import redot.redot_server.support.common.dto.PageResponse;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.support.jwt.cookie.TokenCookieFactory;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.support.security.principal.JwtPrincipal;

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
    public ResponseEntity<PageResponse<CMSMemberResponse>> getCMSMemberList(@CurrentCustomer Long customerId,
                                                                            CMSMemberSearchCondition searchCondition,
                                                                            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                cmsMemberService.getCMSMemberListBySearchCondition(customerId, searchCondition, pageable));
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
                                                @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
                                                @PathVariable(name = "memberId") Long memberId) {
        if (jwtPrincipal.id().equals(memberId)) {
            throw new CMSMemberException(CMSMemberErrorCode.CANNOT_DELETE_SELF);
        }

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
