package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminRedotMemberSearchCondition;
import redot.redot_server.domain.admin.dto.request.AdminRedotMemberUpdateRequest;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberProjection;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberResponse;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.s3.event.ImageDeletionEvent;
import redot.redot_server.global.s3.util.ImageUrlResolver;
import redot.redot_server.global.util.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRedotMemberService {

    private final RedotMemberRepository redotMemberRepository;
    private final RedotAppRepository redotAppRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageUrlResolver imageUrlResolver;

    public PageResponse<AdminRedotMemberResponse> getRedotMembers(AdminRedotMemberSearchCondition searchCondition,
                                                                  Pageable pageable) {
        if (searchCondition != null && searchCondition.status() == RedotMemberStatus.DELETED) {
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_STATUS_FILTER);
        }
        Page<AdminRedotMemberProjection> summaries = redotMemberRepository.searchAdminMembers(searchCondition, pageable);
        Page<AdminRedotMemberResponse> responsePage = summaries.map(summary ->
                AdminRedotMemberResponse.fromProjection(summary, imageUrlResolver));
        return PageResponse.from(responsePage);
    }

    @Transactional
    public AdminRedotMemberResponse updateRedotMember(Long memberId, AdminRedotMemberUpdateRequest request) {
        RedotMember redotMember = getMemberIncludingDeleted(memberId);

        String newProfileImageUrl = imageUrlResolver.toStoredPath(request.profileImageUrl());
        deleteOldProfileImageUrlIfChanged(newProfileImageUrl, redotMember);

        redotMember.updateInfo(request.name(), newProfileImageUrl);
        if (request.status() == RedotMemberStatus.DELETED) {
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_STATUS_UPDATE);
        }

        redotMember.changeStatus(request.status());

        return AdminRedotMemberResponse.fromEntity(redotMember, redotAppRepository.countByOwnerId(memberId), imageUrlResolver);
    }

    @Transactional
    public void deleteRedotMember(Long memberId) {
        RedotMember redotMember = getMemberIncludingDeleted(memberId);
        redotMember.delete();
    }

    private void deleteOldProfileImageUrlIfChanged(String newProfileImageUrl, RedotMember member) {
        String oldProfileImageUrl = member.getProfileImageUrl();
        if (oldProfileImageUrl != null && !oldProfileImageUrl.equals(newProfileImageUrl)) {
            eventPublisher.publishEvent(new ImageDeletionEvent(oldProfileImageUrl));
        }
    }

    private RedotMember getMemberIncludingDeleted(Long memberId) {
        return redotMemberRepository.findByIdIncludingDeleted(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
    }

}
