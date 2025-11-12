package redot.redot_server.domain.cms.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.dto.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.dto.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.dto.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.entity.CMSMember;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.exception.CMSMemberException;
import redot.redot_server.domain.cms.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.exception.CustomerException;
import redot.redot_server.domain.cms.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.repository.CustomerRepository;

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

    public List<CMSMemberResponse> getCMSMemberList(Long customerId) {
        List<CMSMember> cmsMembers = cmsMemberRepository.findAllByCustomer_Id(customerId);

        return cmsMembers.stream()
                .map(cmsMember -> CMSMemberResponse.fromEntity(customerId, cmsMember))
                .toList();
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
        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_Id(memberId, customerId)
                .orElseThrow(() -> new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_NOT_FOUND));

        cmsMember.delete();
    }
}
