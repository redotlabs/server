package redot.redot_server.domain.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.CMSAdminImpersonationRequest;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.cms.entity.CMSMemberRole;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminImpersonationService {

    private final AuthTokenService authTokenService;

    public AuthResult impersonateAsCMSAdmin(CMSAdminImpersonationRequest request, Long adminId) {
        return authTokenService.issueTokens(
                new TokenContext(adminId, TokenType.CMS, List.of(CMSMemberRole.ADMIN.name()), request.customerId())
        );
    }
}
