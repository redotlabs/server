package redot.redot_server.domain.redot.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.dto.RedotMemberUpdateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.s3.service.ImageStorageService;
import redot.redot_server.global.s3.util.ImageDirectory;
import redot.redot_server.global.security.social.model.SocialProfile;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotMemberService {

    private final RedotMemberRepository redotMemberRepository;
    private final ImageStorageService imageStorageService;

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

    @Transactional
    public UploadedImageUrlResponse uploadProfileImage(Long memberId, MultipartFile imageFile) {
        redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));

        String imageUrl = imageStorageService.upload(ImageDirectory.REDOT_MEMBER_PROFILE, memberId, imageFile);
        return new UploadedImageUrlResponse(imageUrl);
    }

    @Transactional
    public RedotMemberResponse updateRedotMemberInfo(Long id, RedotMemberUpdateRequest request) {
        RedotMember redotMember = redotMemberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));

        deleteOldProfileImageUrlIfChanged(request, redotMember);

        redotMember.updateInfo(request.name(), request.profileImageUrl());

        return RedotMemberResponse.fromEntity(redotMember);
    }

    private void deleteOldProfileImageUrlIfChanged(RedotMemberUpdateRequest request, RedotMember redotMember) {
        String oldProfileImageUrl = redotMember.getProfileImageUrl();
        if (oldProfileImageUrl != null && !oldProfileImageUrl.equals(request.profileImageUrl())) {
            imageStorageService.delete(oldProfileImageUrl);
        }
    }
}
