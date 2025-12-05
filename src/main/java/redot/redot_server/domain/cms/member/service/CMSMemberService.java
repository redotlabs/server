package redot.redot_server.domain.cms.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.redot.app.exception.RedotAppErrorCode;
import redot.redot_server.domain.redot.app.exception.RedotAppException;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.global.util.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSMemberService {

    private final CMSMemberRepository cmsMemberRepository;
    private final RedotAppRepository redotAppRepository;
    private final PasswordEncoder passwordEncoder;

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

        cmsMember.updateProfile(request.name(), request.profileImageUrl());

        return CMSMemberResponse.fromEntity(redotAppId, cmsMember);
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
}
