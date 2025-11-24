package redot.redot_server.domain.cms.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.cms.inquiry.dto.RedotAppInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiry;

public interface RedotAppInquiryRepositoryCustom {
    Page<RedotAppInquiry> findAllBySearchCondition(Long redotAppId,
                                                   RedotAppInquirySearchCondition searchCondition,
                                                   Pageable pageable);
}
