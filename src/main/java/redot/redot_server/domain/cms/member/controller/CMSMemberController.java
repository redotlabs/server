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
import redot.redot_server.domain.cms.member.dto.request.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.member.service.CMSMemberService;
import redot.redot_server.global.common.dto.response.PageResponse;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.jwt.cookie.TokenCookieFactory;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/cms/members")
public class CMSMemberController {

    private final CMSMemberService cmsMemberService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping
    public ResponseEntity<CMSMemberResponse> createCMSMember(@CurrentRedotApp Long redotAppId,
                                                             @RequestBody @Valid CMSMemberCreateRequest request) {
        return ResponseEntity.ok(cmsMemberService.createCMSMember(redotAppId, request));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<CMSMemberResponse> getCMSMemberInfo(@CurrentRedotApp Long redotAppId,
                                                              @PathVariable(name = "memberId") Long memberId) {
        return ResponseEntity.ok(cmsMemberService.getCMSMemberInfo(redotAppId, memberId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CMSMemberResponse>> getCMSMemberList(@CurrentRedotApp Long redotAppId,
                                                                            CMSMemberSearchCondition searchCondition,
                                                                            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                cmsMemberService.getCMSMemberListBySearchCondition(redotAppId, searchCondition, pageable));
    }

    @PatchMapping("/role/{memberId}")
    public ResponseEntity<CMSMemberResponse> changeCMSMemberRole(@CurrentRedotApp Long redotAppId,
                                                                 @PathVariable(name = "memberId") Long memberId,
                                                                 @RequestBody @Valid CMSMemberRoleRequest request) {
        return ResponseEntity.ok(cmsMemberService.changeCMSMemberRole(redotAppId, memberId, request));
    }

    @PutMapping
    public ResponseEntity<CMSMemberResponse> updateCMSMember(@CurrentRedotApp Long redotAppId,
                                                             @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
                                                             @RequestBody @Valid CMSMemberUpdateRequest request) {
        return ResponseEntity.ok(cmsMemberService.updateCMSMember(redotAppId, jwtPrincipal.id(), request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteCMSMember(@CurrentRedotApp Long redotAppId,
                                                @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
                                                @PathVariable(name = "memberId") Long memberId) {
        if (jwtPrincipal.id().equals(memberId)) {
            throw new CMSMemberException(CMSMemberErrorCode.CANNOT_DELETE_SELF);
        }

        cmsMemberService.deleteCMSMember(redotAppId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentCMSMember(HttpServletRequest request,
                                                       @CurrentRedotApp Long redotAppId,
                                                       @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        cmsMemberService.deleteCMSMember(redotAppId, jwtPrincipal.id());
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
