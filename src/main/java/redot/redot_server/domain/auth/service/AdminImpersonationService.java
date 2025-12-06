package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.request.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminImpersonationService {

    private final AuthTokenService authTokenService;

    public AuthResult impersonateAsCMSAdmin(HttpServletRequest request, CMSAdminImpersonationRequest cmsAdminImpersonationRequest, Long adminId) {
        return authTokenService.issueTokens(request,
                new TokenContext(adminId, TokenType.CMS, List.of(CMSMemberRole.ADMIN.name()), cmsAdminImpersonationRequest.redotAppId())
        );
    }
}
