package redot.redot_server.domain.redot.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.dto.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.support.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotMemberService {

    private final RedotMemberRepository redotMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RedotMemberResponse createRedotMember(RedotMemberCreateRequest request) {
        final String normalizedEmail = EmailUtils.normalize(request.email());

        if (redotMemberRepository.existsByEmail(normalizedEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            RedotMember redotMember = RedotMember.create(
                    request.name(),
                    normalizedEmail,
                    passwordEncoder.encode(request.password()),
                    null
            );

            RedotMember savedMember = redotMemberRepository.save(redotMember);

            return RedotMemberResponse.from(savedMember);
        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex);
        }
    }
}
