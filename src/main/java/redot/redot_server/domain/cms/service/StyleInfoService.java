package redot.redot_server.domain.cms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.cms.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.dto.StyleInfoUpdateRequest;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.entity.StyleInfo;
import redot.redot_server.domain.cms.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.exception.CustomerException;
import redot.redot_server.domain.cms.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.exception.StyleInfoException;
import redot.redot_server.domain.cms.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StyleInfoService {
    private final CustomerRepository customerRepository;
    private final StyleInfoRepository styleInfoRepository;


    public StyleInfoResponse getStyleInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByCustomer_Id(customer.getId())
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));
        return StyleInfoResponse.fromEntity(styleInfo);
    }

    @Transactional
    public StyleInfoResponse updateStyleInfo(Long customerId, StyleInfoUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByCustomer_Id(customer.getId())
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        styleInfo.update(request.font(), request.color(), request.theme());

        return StyleInfoResponse.fromEntity(styleInfo);
    }
}
