package redot.redot_server.domain.redot.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.dto.RedotMemberUpdateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.s3.event.ImageDeletionEvent;
import redot.redot_server.global.s3.service.ImageStorageService;
import redot.redot_server.global.s3.util.ImageDirectory;
import redot.redot_server.global.s3.util.ImageUrlResolver;
import redot.redot_server.global.security.social.model.SocialProfile;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotMemberService {

    private final RedotMemberRepository redotMemberRepository;
    private final ImageStorageService imageStorageService;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageUrlResolver imageUrlResolver;
    private final RedotMemberStatusValidator redotMemberStatusValidator;

    @Transactional
    public RedotMember findOrCreateSocialMember(SocialProfile profile, SocialProvider provider) {
        String normalizedEmail = EmailUtils.normalize(profile.email());
        if (!StringUtils.hasText(normalizedEmail)) {
            throw new AuthException(AuthErrorCode.SOCIAL_EMAIL_REQUIRED);
        }

        return redotMemberRepository
                .findBySocialProviderAndSocialProviderId(provider, profile.providerId())
                .map(existing -> {
                    redotMemberStatusValidator.ensureActive(existing);
                    return existing;
                })
                .or(() -> redotMemberRepository.findByEmail(normalizedEmail)
                        .map(existing -> {
                            redotMemberStatusValidator.ensureActive(existing);
                            existing.linkSocialAccount(provider, profile.providerId(), profile.name(), profile.profileImageUrl());
                            return existing;
                        }))
                .orElseGet(() -> redotMemberRepository.save(RedotMember.createSocialMember(
                        profile.name(),
                        normalizedEmail,
                        profile.profileImageUrl(),
                        provider,
                        profile.providerId()
                )));
    }

    @Transactional
    public UploadedImageUrlResponse uploadProfileImage(Long memberId, MultipartFile imageFile) {
        RedotMember member = redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        redotMemberStatusValidator.ensureActive(member);

        String imageUrl = imageStorageService.upload(ImageDirectory.REDOT_MEMBER_PROFILE, memberId, imageFile);
        return new UploadedImageUrlResponse(imageUrl);
    }

    @Transactional
    public RedotMemberResponse updateRedotMemberInfo(Long id, RedotMemberUpdateRequest request) {
        RedotMember redotMember = redotMemberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));

        redotMemberStatusValidator.ensureActive(redotMember);

        String newProfileImageUrl = imageUrlResolver.toStoredPath(request.profileImageUrl());

        deleteOldProfileImageUrlIfChanged(newProfileImageUrl, redotMember);

        redotMember.updateInfo(request.name(), newProfileImageUrl);

        return RedotMemberResponse.fromEntity(redotMember, imageUrlResolver);
    }

    private void deleteOldProfileImageUrlIfChanged(String newProfileImageUrl, RedotMember redotMember) {
        String oldProfileImageUrl = redotMember.getProfileImageUrl();
        if (oldProfileImageUrl != null && !oldProfileImageUrl.equals(newProfileImageUrl)) {
            eventPublisher.publishEvent(new ImageDeletionEvent(oldProfileImageUrl));
        }
    }

    @Transactional
    public void deleteRedotMember(Long id) {
        RedotMember redotMember = redotMemberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        redotMemberStatusValidator.ensureActive(redotMember);
        redotMember.delete();
    }
}
