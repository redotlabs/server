package redot.redot_server.domain.cms.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiry;

public interface CustomInquiryRepositoryCustom {
    Page<CustomerInquiry> findAllBySearchCondition(Long customerId,
                                                   CustomerInquirySearchCondition searchCondition,
                                                   Pageable pageable);
}
