package redot.redot_server.domain.cms.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.member.dto.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.member.dto.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.customer.entity.Customer;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;
import redot.redot_server.domain.cms.customer.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.customer.exception.CustomerException;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.customer.repository.CustomerRepository;
import redot.redot_server.support.common.dto.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSMemberService {

    private final CMSMemberRepository cmsMemberRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CMSMemberResponse createCMSMember(Long customerId, CMSMemberCreateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        CMSMember cmsMember = cmsMemberRepository.save(
                CMSMember.join(customer, request.name(), request.email(),
                        passwordEncoder.encode(request.password()),
                        request.role()));
        return CMSMemberResponse.fromEntity(customerId, cmsMember);
    }

    public CMSMemberResponse getCMSMemberInfo(Long customerId, Long memberId) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(memberId, customerId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        return CMSMemberResponse.fromEntity(customerId, cmsMember);
    }

    @Transactional
    public CMSMemberResponse changeCMSMemberRole(Long customerId, Long memberId, CMSMemberRoleRequest request) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(memberId, customerId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        cmsMember.changeRole(request.role());

        return CMSMemberResponse.fromEntity(customerId, cmsMember);
    }

    @Transactional
    public CMSMemberResponse updateCMSMember(Long customerId, Long memberId, CMSMemberUpdateRequest request) {
        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(memberId, customerId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        cmsMember.updateProfile(request.name(), request.profileImageUrl());

        return CMSMemberResponse.fromEntity(customerId, cmsMember);
    }

    @Transactional
    public void deleteCMSMember(Long customerId, Long memberId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(memberId, customerId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        if(customer.getOwner() == cmsMember) {
            throw new CMSMemberException(CMSMemberErrorCode.CANNOT_DELETE_OWNER);
        }

        cmsMember.delete();
    }

    public PageResponse<CMSMemberResponse> getCMSMemberListBySearchCondition(Long customerId,
                                                                             CMSMemberSearchCondition searchCondition,
                                                                             Pageable pageable) {
        Page<CMSMemberResponse> page = cmsMemberRepository
                .findAllBySearchCondition(customerId, searchCondition, pageable)
                .map(cmsMember -> CMSMemberResponse.fromEntity(customerId, cmsMember));

        return PageResponse.from(page);
    }
}
