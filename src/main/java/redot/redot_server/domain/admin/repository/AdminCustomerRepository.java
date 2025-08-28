package redot.redot_server.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.admin.entity.AdminCustomer;

public interface AdminCustomerRepository extends JpaRepository<AdminCustomer, Long> {
    boolean existsBySchemaName(String schemaName);
    boolean existsByDomainName(String domainName);
}
