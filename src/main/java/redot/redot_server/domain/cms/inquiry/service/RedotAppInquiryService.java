package redot.redot_server.domain.cms.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppErrorCode;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppException;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.dto.response.RedotAppInquiryResponse;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiry;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryErrorCode;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryException;
import redot.redot_server.domain.cms.inquiry.repository.RedotAppInquiryRepository;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.global.common.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotAppInquiryService {

    private final RedotAppRepository redotAppRepository;
    private final RedotAppInquiryRepository redotAppInquiryRepository;
    private final InquiryNumberGenerator inquiryNumberGenerator;
    private final CMSMemberRepository cmsMemberRepository;

    @Transactional
    public RedotAppInquiryResponse createInquiry(Long redotAppId, RedotAppInquiryCreateRequest request) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId).orElseThrow(()-> new RedotAppException(
                RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        String inquiryNumber = inquiryNumberGenerator.generateInquiryNumber();

        RedotAppInquiry savedInquiry = redotAppInquiryRepository.save(RedotAppInquiry.create(
                redotApp,
                inquiryNumber,
                request.inquirerName(),
                request.title(),
                request.content()
        ));

        return new RedotAppInquiryResponse(
                savedInquiry.getId(),
                redotAppId,
                savedInquiry.getInquiryNumber(),
                savedInquiry.getInquirerName(),
                savedInquiry.getTitle(),
                savedInquiry.getContent(),
                savedInquiry.getStatus(),
                savedInquiry.getCreatedAt()
        );
    }

    public RedotAppInquiryResponse getInquiry(Long redotAppId, Long inquiryId) {
        RedotAppInquiry inquiry = redotAppInquiryRepository.findByIdAndRedotApp_Id(inquiryId, redotAppId)
                .orElseThrow(() -> new RedotAppInquiryException(RedotAppInquiryErrorCode.REDOT_APP_INQUIRY_NOT_FOUND));

        return new RedotAppInquiryResponse(
                inquiry.getId(),
                redotAppId,
                inquiry.getInquiryNumber(),
                inquiry.getInquirerName(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getCreatedAt()
        );
    }

    @Transactional
    public void markInquiryAsCompleted(Long redotAppId, Long inquiryId, Long cmsMemberId) {
        RedotAppInquiry inquiry = redotAppInquiryRepository.findByIdAndRedotApp_Id(inquiryId, redotAppId)
                .orElseThrow(() -> new RedotAppInquiryException(RedotAppInquiryErrorCode.REDOT_APP_INQUIRY_NOT_FOUND));

        CMSMember assignee = cmsMemberRepository.findById(cmsMemberId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));
        inquiry.processInquiry(assignee);
    }

    @Transactional
    public void reopenInquiry(Long redotAppId, Long inquiryId) {
        RedotAppInquiry inquiry = redotAppInquiryRepository.findByIdAndRedotApp_Id(inquiryId, redotAppId)
                .orElseThrow(() -> new RedotAppInquiryException(RedotAppInquiryErrorCode.REDOT_APP_INQUIRY_NOT_FOUND));

        inquiry.reopenInquiry();
    }

    public PageResponse<RedotAppInquiryResponse> getAllInquiriesBySearchCondition(Long redotAppId,
                                                                                  RedotAppInquirySearchCondition searchCondition,
                                                                                  Pageable pageable) {
        Page<RedotAppInquiryResponse> page = redotAppInquiryRepository
                .findAllBySearchCondition(redotAppId, searchCondition, pageable)
                .map(inquiry -> new RedotAppInquiryResponse(
                        inquiry.getId(),
                        redotAppId,
                        inquiry.getInquiryNumber(),
                        inquiry.getInquirerName(),
                        inquiry.getTitle(),
                        inquiry.getContent(),
                        inquiry.getStatus(),
                        inquiry.getCreatedAt()
                ));
        return PageResponse.from(page);
    }
}
