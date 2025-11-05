package redot.redot_server.domain.cms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.exception.DomainErrorCode;
import redot.redot_server.domain.admin.exception.DomainException;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.cms.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.dto.CustomerInfoResponse;
import redot.redot_server.domain.cms.dto.CustomerResponse;
import redot.redot_server.domain.cms.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.entity.CMSMember;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.entity.SiteSetting;
import redot.redot_server.domain.cms.entity.StyleInfo;
import redot.redot_server.domain.cms.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.exception.CustomerException;
import redot.redot_server.domain.cms.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.exception.SiteSettingException;
import redot.redot_server.domain.cms.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.exception.StyleInfoException;
import redot.redot_server.domain.cms.repository.CustomerRepository;
import redot.redot_server.domain.cms.repository.SiteSettingRepository;

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

        return new CustomerInfoResponse(
                CustomerResponse.fromEntity(customer),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                CMSMemberResponse.fromEntity(customer.getId(), owner)
        );
    }

}
