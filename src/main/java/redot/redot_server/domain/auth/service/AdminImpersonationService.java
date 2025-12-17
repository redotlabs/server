package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.request.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminImpersonationService {

    private final AuthTokenService authTokenService;
    private final CMSMemberRepository cmsMemberRepository;

    public AuthResult impersonateAsCMSAdmin(HttpServletRequest request,
                                            CMSAdminImpersonationRequest cmsAdminImpersonationRequest) {
        CMSMember owner = cmsMemberRepository
                .findFirstByRedotApp_IdAndRoleOrderByIdAsc(cmsAdminImpersonationRequest.redotAppId(),
                        CMSMemberRole.OWNER)
                .orElseThrow(() -> new AuthException(AuthErrorCode.CMS_MEMBER_NOT_FOUND));

        return authTokenService.issueTokens(request,
                new TokenContext(owner.getId(), TokenType.CMS, List.of(owner.getRole().name()),
                        cmsAdminImpersonationRequest.redotAppId())
        );
    }
}
