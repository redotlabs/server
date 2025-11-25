package redot.redot_server.domain.cms.inquiry.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiry;

public interface RedotAppInquiryRepository extends JpaRepository<RedotAppInquiry, Long>,
        RedotAppInquiryRepositoryCustom {
    Optional<RedotAppInquiry> findByIdAndRedotApp_Id(Long inquiryId, Long redotAppId);
}
