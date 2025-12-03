package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.support.jwt.token.TokenContext;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.support.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.support.security.filter.jwt.refresh.RefreshTokenPayloadHolder;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSAuthService {

    private final CMSMemberRepository cmsMemberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(HttpServletRequest request, SignInRequest signInRequest, Long redotAppId) {
        CMSMember cmsMember = cmsMemberRepository.findByEmailAndRedotApp_Id(signInRequest.email(), redotAppId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (!passwordEncoder.matches(signInRequest.password(), cmsMember.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_USER_INFO);
        }

        return authTokenService.issueTokens(
                request,
                new TokenContext(cmsMember.getId(), TokenType.CMS, List.of(cmsMember.getRole().name()), redotAppId)
        );
    }

    public AuthResult reissueToken(Long redotAppId, HttpServletRequest request) {
        RefreshTokenPayload payload = RefreshTokenPayloadHolder.get(request);

        if (payload == null) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }

        Long cmsMemberId = payload.subjectId();
        if (cmsMemberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        Long tokenRedotAppId = payload.redotAppId();
        if (tokenRedotAppId == null || !tokenRedotAppId.equals(redotAppId)) {
            throw new AuthException(AuthErrorCode.REDOT_APP_TOKEN_MISMATCH);
        }

        return authTokenService.issueTokens(request, new TokenContext(
                cmsMemberId,
                payload.tokenType(),
                payload.roles(),
                tokenRedotAppId
        ));
    }

    public CMSMemberResponse getCurrentCMSMemberInfo(Long redotAppId, Long id) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_Id(id, redotAppId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.CMS_MEMBER_NOT_FOUND));

        return new CMSMemberResponse(
                redotAppId,
                cmsMember.getId(),
                cmsMember.getName(),
                cmsMember.getEmail(),
                cmsMember.getProfileImageUrl(),
                cmsMember.getRole(),
                cmsMember.getCreatedAt()
        );
    }
}
