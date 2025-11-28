package redot.redot_server.domain.redot.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.support.security.social.model.SocialProfile;
import redot.redot_server.support.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotMemberService {

    private final RedotMemberRepository redotMemberRepository;

    @Transactional
    public RedotMember findOrCreateSocialMember(SocialProfile profile, SocialProvider provider) {
        String normalizedEmail = EmailUtils.normalize(profile.email());

        Optional<RedotMember> byProvider = redotMemberRepository
                .findBySocialProviderAndSocialProviderId(provider, profile.providerId());
        if (byProvider.isPresent()) {
            return byProvider.get();
        }

        Optional<RedotMember> byEmail = redotMemberRepository.findByEmail(normalizedEmail);
        if (byEmail.isPresent()) {
            RedotMember existing = byEmail.get();
            existing.linkSocialAccount(provider, profile.providerId(), profile.name(), profile.profileImageUrl());
            return existing;
        }

        RedotMember socialMember = RedotMember.createSocialMember(
                profile.name(),
                normalizedEmail,
                profile.profileImageUrl(),
                provider,
                profile.providerId()
        );
        return redotMemberRepository.save(socialMember);
    }

}
