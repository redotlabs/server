package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.admin.util.SubDomainNameGenerator;
import redot.redot_server.domain.cms.member.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.customer.dto.CustomerCreateRequest;
import redot.redot_server.domain.cms.customer.dto.CustomerInfoResponse;
import redot.redot_server.domain.cms.customer.dto.CustomerResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.domain.cms.customer.entity.Customer;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.customer.repository.CustomerRepository;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCustomerService {
    private final CustomerRepository customerRepository;
    private final CMSMemberRepository cmsMemberRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerInfoResponse createCustomer(CustomerCreateRequest request) {
        Customer customer = customerRepository.save(Customer.create(request.companyName()));

        String domainName = SubDomainNameGenerator.generateSubdomain();
        Domain domain = domainRepository.save(Domain.ofCustomer(domainName, customer));

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(customer));

        StyleInfo styleInfo = styleInfoRepository.save(
                StyleInfo.create(
                        request.font(),
                        request.color(),
                        request.theme(),
                        customer
                )
        );

        CMSMember owner = cmsMemberRepository.save(CMSMember.create(
                        customer,
                        request.ownerName(),
                        request.ownerEmail(),
                        request.ownerProfileImageUrl(),
                        passwordEncoder.encode(request.ownerPassword()),
                        CMSMemberRole.ADMIN
                )
        );

        customer.setOwner(owner);

        return new CustomerInfoResponse(
                CustomerResponse.fromEntity(customer),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                CMSMemberResponse.fromEntity(customer.getId(), owner)
        );
    }
}
