package redot.redot_server.domain.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
