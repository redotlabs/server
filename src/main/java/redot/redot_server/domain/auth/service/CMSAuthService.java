package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.dto.CMSMemberDTO;
import redot.redot_server.domain.cms.entity.CMSMember;
import redot.redot_server.domain.cms.repository.CMSMemberRepository;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayloadHolder;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSAuthService {

    private final CMSMemberRepository cmsMemberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(HttpServletRequest request, SignInRequest signInRequest, Long customerId) {
        CMSMember cmsMember = cmsMemberRepository.findByEmailAndCustomer_Id(signInRequest.email(), customerId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (!passwordEncoder.matches(signInRequest.password(), cmsMember.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_USER_INFO);
        }

        return authTokenService.issueTokens(
                request,
                new TokenContext(cmsMember.getId(), TokenType.CMS, List.of(cmsMember.getRole().name()), customerId)
        );
    }

    public AuthResult reissueToken(Long customerId, HttpServletRequest request) {
        RefreshTokenPayload payload = RefreshTokenPayloadHolder.get(request);

        if (payload == null) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }

        Long cmsMemberId = payload.subjectId();
        if (cmsMemberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        Long tokenCustomerId = payload.customerId();
        if (tokenCustomerId == null || !tokenCustomerId.equals(customerId)) {
            throw new AuthException(AuthErrorCode.CUSTOMER_TOKEN_MISMATCH);
        }

        return authTokenService.issueTokens(request, new TokenContext(
                cmsMemberId,
                payload.tokenType(),
                payload.roles(),
                tokenCustomerId
        ));
    }

    public CMSMemberDTO getCurrentCMSMemberInfo(Long customerId, Long id) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.CMS_MEMBER_NOT_FOUND));

        return new CMSMemberDTO(
                cmsMember.getCustomer().getId(),
                cmsMember.getId(),
                cmsMember.getName(),
                cmsMember.getEmail(),
                cmsMember.getProfileImageUrl(),
                cmsMember.getRole(),
                cmsMember.getCreatedAt()
        );
    }
}
