package redot.redot_server.domain.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.cms.entity.CMSMember;
import redot.redot_server.domain.cms.repository.CMSMemberRepository;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSAuthService {

    private final CMSMemberRepository cmsMemberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(SignInRequest request, Long customerId) {
        CMSMember cmsMember = cmsMemberRepository.findByEmailAndCustomer_Id(request.email(), customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), cmsMember.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return authTokenService.issueTokens(
                new TokenContext(cmsMember.getId(), TokenType.CMS, List.of(cmsMember.getRole().name()), customerId)
        );
    }
}
