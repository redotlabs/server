package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.admin.util.DomainNameGenerator;
import redot.redot_server.domain.cms.dto.CMSMemberDTO;
import redot.redot_server.domain.cms.dto.CustomerCreateRequest;
import redot.redot_server.domain.cms.dto.CustomerCreateResponse;
import redot.redot_server.domain.cms.entity.CMSMember;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.entity.SiteSetting;
import redot.redot_server.domain.cms.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.repository.CustomerRepository;
import redot.redot_server.domain.cms.repository.SiteSettingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCustomerService {
    private final CustomerRepository customerRepository;
    private final CMSMemberRepository cmsMemberRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerCreateResponse createCustomer(CustomerCreateRequest request) {
        Customer customer = customerRepository.save(Customer.create(request.companyName()));

        String domainName = DomainNameGenerator.generateSubdomain();
        Domain domain = domainRepository.save(Domain.ofCustomer(domainName, customer));

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(customer, request.theme()));

        String encodedPassword = passwordEncoder.encode(request.password());
        CMSMember owner = cmsMemberRepository.save(CMSMember.create(
                        customer,
                        request.name(),
                        request.ownerEmail(),
                        encodedPassword,
                        request.role()
                )
        );

        customer.setOwner(owner);

        return new CustomerCreateResponse(
                customer.getId(),
                customer.getCompanyName(),
                siteSetting.getTheme(),
                domain.getDomainName(),
                new CMSMemberDTO(
                        customer.getId(),
                        owner.getId(),
                        owner.getName(),
                        owner.getEmail(),
                        owner.getRole()
                )
        );
    }
}
