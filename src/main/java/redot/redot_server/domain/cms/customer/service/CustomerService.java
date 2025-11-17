package redot.redot_server.domain.cms.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.exception.DomainErrorCode;
import redot.redot_server.domain.admin.exception.DomainException;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.cms.member.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.customer.dto.CustomerInfoResponse;
import redot.redot_server.domain.cms.customer.dto.CustomerResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.customer.entity.Customer;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.customer.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.customer.exception.CustomerException;
import redot.redot_server.domain.cms.site.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.site.exception.SiteSettingException;
import redot.redot_server.domain.cms.style.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.style.exception.StyleInfoException;
import redot.redot_server.domain.cms.customer.repository.CustomerRepository;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;

    public CustomerInfoResponse getCustomerInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND)
        );

        Domain domain = domainRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        SiteSetting siteSetting = siteSettingRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

        CMSMember owner = customer.getOwner();

        if (owner == null) {
            throw new CustomerException(CustomerErrorCode.CUSTOMER_OWNER_NOT_FOUND);
        }

        return new CustomerInfoResponse(
                CustomerResponse.fromEntity(customer),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                CMSMemberResponse.fromEntity(customer.getId(), owner)
        );
    }

}
