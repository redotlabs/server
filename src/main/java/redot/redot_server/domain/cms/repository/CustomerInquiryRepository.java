package redot.redot_server.domain.cms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.CustomerInquiry;

public interface CustomerInquiryRepository extends JpaRepository<CustomerInquiry, Long> {
    Optional<CustomerInquiry> findByIdAndCustomer_Id(Long inquiryId, Long customerId);

    List<CustomerInquiry> findAllByCustomerId(Long customerId);
}
