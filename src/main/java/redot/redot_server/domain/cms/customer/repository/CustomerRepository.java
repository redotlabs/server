package redot.redot_server.domain.cms.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
