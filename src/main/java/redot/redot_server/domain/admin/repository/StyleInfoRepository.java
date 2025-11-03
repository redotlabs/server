package redot.redot_server.domain.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.StyleInfo;

public interface StyleInfoRepository extends JpaRepository<StyleInfo, Long> {
    Optional<StyleInfo> findByCustomer_Id(Long customerId);
}
