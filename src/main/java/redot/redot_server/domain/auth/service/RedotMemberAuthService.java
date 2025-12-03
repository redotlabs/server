package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.request.RedotMemberSignInRequest;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.domain.redot.member.dto.request.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayloadHolder;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotMemberAuthService {

    private final RedotMemberRepository redotMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public RedotMemberResponse signUp(RedotMemberCreateRequest request) {
        String normalizedEmail = EmailUtils.normalize(request.email());

        if (redotMemberRepository.existsByEmail(normalizedEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        emailVerificationService.consumeVerifiedToken(
                EmailVerificationPurpose.REDOT_MEMBER_SIGN_UP,
                normalizedEmail,
                request.verificationToken()
        );

        RedotMember redotMember = RedotMember.create(
                request.name(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                null
        );

        RedotMember savedMember = redotMemberRepository.save(redotMember);
        return RedotMemberResponse.from(savedMember);
    }

    public AuthResult signIn(HttpServletRequest request, RedotMemberSignInRequest signInRequest) {
        String normalizedEmail = EmailUtils.normalize(signInRequest.email());

        RedotMember member = redotMemberRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (member.getPassword() == null || !passwordEncoder.matches(signInRequest.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_USER_INFO);
        }

        TokenContext context = new TokenContext(
                member.getId(),
                TokenType.REDOT_MEMBER,
                null,
                null
        );

        return authTokenService.issueTokens(request, context);
    }

    public AuthResult reissue(HttpServletRequest request) {
        RefreshTokenPayload payload = RefreshTokenPayloadHolder.get(request);

        if (payload == null) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }

        if (payload.tokenType() != TokenType.REDOT_MEMBER) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        Long memberId = payload.subjectId();
        if (memberId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));

        return authTokenService.issueTokens(request, new TokenContext(
                memberId,
                TokenType.REDOT_MEMBER,
                payload.roles(),
                null
        ));
    }

    public RedotMemberResponse getCurrentMember(Long memberId) {
        RedotMember member = redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));

        return RedotMemberResponse.from(member);
    }
}
