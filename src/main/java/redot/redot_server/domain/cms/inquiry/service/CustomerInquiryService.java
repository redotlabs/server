package redot.redot_server.domain.cms.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.customer.entity.Customer;
import redot.redot_server.domain.cms.customer.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.customer.exception.CustomerException;
import redot.redot_server.domain.cms.customer.repository.CustomerRepository;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquiryDTO;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiry;
import redot.redot_server.domain.cms.inquiry.exception.CustomerInquiryErrorCode;
import redot.redot_server.domain.cms.inquiry.exception.CustomerInquiryException;
import redot.redot_server.domain.cms.inquiry.repository.CustomerInquiryRepository;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.support.common.dto.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerInquiryService {

    private final CustomerRepository customerRepository;
    private final CustomerInquiryRepository customerInquiryRepository;
    private final InquiryNumberGenerator inquiryNumberGenerator;
    private final CMSMemberRepository cmsMemberRepository;

    @Transactional
    public CustomerInquiryDTO createInquiry(Long customerId, CustomerInquiryCreateRequest request) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(()-> new CustomerException(
                CustomerErrorCode.CUSTOMER_NOT_FOUND));

        String inquiryNumber = inquiryNumberGenerator.generateInquiryNumber();

        CustomerInquiry savedInquiry = customerInquiryRepository.save(CustomerInquiry.create(
                customer,
                inquiryNumber,
                request.inquirerName(),
                request.title(),
                request.content()
        ));

        return new CustomerInquiryDTO(
                savedInquiry.getId(),
                customerId,
                savedInquiry.getInquiryNumber(),
                savedInquiry.getInquirerName(),
                savedInquiry.getTitle(),
                savedInquiry.getContent(),
                savedInquiry.getStatus(),
                savedInquiry.getCreatedAt()
        );
    }

    public CustomerInquiryDTO getInquiry(Long customerId, Long inquiryId) {
        CustomerInquiry inquiry = customerInquiryRepository.findByIdAndCustomer_Id(inquiryId, customerId)
                .orElseThrow(() -> new CustomerInquiryException(CustomerInquiryErrorCode.CUSTOMER_INQUIRY_NOT_FOUND));

        return new CustomerInquiryDTO(
                inquiry.getId(),
                customerId,
                inquiry.getInquiryNumber(),
                inquiry.getInquirerName(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getCreatedAt()
        );
    }

    @Transactional
    public void markInquiryAsCompleted(Long customerId, Long inquiryId, Long cmsMemberId) {
        CustomerInquiry inquiry = customerInquiryRepository.findByIdAndCustomer_Id(inquiryId, customerId)
                .orElseThrow(() -> new CustomerInquiryException(CustomerInquiryErrorCode.CUSTOMER_INQUIRY_NOT_FOUND));

        CMSMember assignee = cmsMemberRepository.findById(cmsMemberId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));
        inquiry.processInquiry(assignee);
    }

    @Transactional
    public void reopenInquiry(Long customerId, Long inquiryId) {
        CustomerInquiry inquiry = customerInquiryRepository.findByIdAndCustomer_Id(inquiryId, customerId)
                .orElseThrow(() -> new CustomerInquiryException(CustomerInquiryErrorCode.CUSTOMER_INQUIRY_NOT_FOUND));

        inquiry.reopenInquiry();
    }

    public PageResponse<CustomerInquiryDTO> getAllInquiriesBySearchCondition(Long customerId,
                                                                             CustomerInquirySearchCondition searchCondition,
                                                                             Pageable pageable) {
        Page<CustomerInquiryDTO> page = customerInquiryRepository
                .findAllBySearchCondition(customerId, searchCondition, pageable)
                .map(inquiry -> new CustomerInquiryDTO(
                        inquiry.getId(),
                        customerId,
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
