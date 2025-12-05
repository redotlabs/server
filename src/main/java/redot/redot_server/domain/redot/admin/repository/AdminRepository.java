package redot.redot_server.domain.redot.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.redot.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM admins WHERE id = :id", nativeQuery = true)
    Optional<Admin> findByIdIncludingDeleted(@Param("id") Long id);

}
