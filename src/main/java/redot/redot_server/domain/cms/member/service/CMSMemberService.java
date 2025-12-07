package redot.redot_server.domain.cms.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.exception.RedotAppErrorCode;
import redot.redot_server.domain.redot.app.exception.RedotAppException;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.s3.event.ImageDeletionEvent;
import redot.redot_server.global.s3.service.ImageStorageService;
import redot.redot_server.global.s3.util.ImageDirectory;
import redot.redot_server.global.util.dto.response.PageResponse;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSMemberService {

    private final CMSMemberRepository cmsMemberRepository;
    private final RedotAppRepository redotAppRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CMSMemberResponse createCMSMember(Long redotAppId, CMSMemberCreateRequest request) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        CMSMember cmsMember = cmsMemberRepository.save(
                CMSMember.join(redotApp, request.name(), request.email(),
                        passwordEncoder.encode(request.password()),
                        request.role()));
        return CMSMemberResponse.fromEntity(redotAppId, cmsMember);
    }

    public CMSMemberResponse getCMSMemberInfo(Long redotAppId, Long memberId) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_Id(memberId, redotAppId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        return CMSMemberResponse.fromEntity(redotAppId, cmsMember);
    }

    @Transactional
    public CMSMemberResponse changeCMSMemberRole(Long redotAppId, Long memberId, CMSMemberRoleRequest request) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_Id(memberId, redotAppId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        cmsMember.changeRole(request.role());

        return CMSMemberResponse.fromEntity(redotAppId, cmsMember);
    }

    @Transactional
    public CMSMemberResponse updateCMSMember(Long redotAppId, Long memberId, CMSMemberUpdateRequest request) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_Id(memberId, redotAppId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        deleteOldProfileImageUrlIfChanged(request, cmsMember);

        cmsMember.updateProfile(request.name(), request.profileImageUrl());

        return CMSMemberResponse.fromEntity(redotAppId, cmsMember);
    }

    private void deleteOldProfileImageUrlIfChanged(CMSMemberUpdateRequest request, CMSMember cmsMember) {
        String oldProfileImageUrl = cmsMember.getProfileImageUrl();
        if (oldProfileImageUrl != null && !oldProfileImageUrl.equals(request.profileImageUrl())) {
            eventPublisher.publishEvent(new ImageDeletionEvent(oldProfileImageUrl));
        }
    }

    @Transactional
    public void deleteCMSMember(Long redotAppId, Long memberId) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_Id(memberId, redotAppId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        cmsMember.delete();
    }

    public PageResponse<CMSMemberResponse> getCMSMemberListBySearchCondition(Long redotAppId,
                                                                             CMSMemberSearchCondition searchCondition,
                                                                             Pageable pageable) {
        Page<CMSMemberResponse> page = cmsMemberRepository
                .findAllBySearchCondition(redotAppId, searchCondition, pageable)
                .map(cmsMember -> CMSMemberResponse.fromEntity(redotAppId, cmsMember));

        return PageResponse.from(page);
    }

    @Transactional
    public UploadedImageUrlResponse uploadProfileImage(Long redotAppId, Long memberId, MultipartFile imageFile) {
        cmsMemberRepository.findByIdAndRedotApp_Id(memberId, redotAppId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        String imageUrl = imageStorageService.upload(ImageDirectory.CMS_MEMBER_PROFILE, memberId, imageFile);
        return new UploadedImageUrlResponse(imageUrl);
    }
}
