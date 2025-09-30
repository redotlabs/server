package redot.redot_server.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
