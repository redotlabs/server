package redot.redot_server.domain.cms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.CMSMember;

public interface CMSMemberRepository extends JpaRepository<CMSMember, Long> {
    Optional<CMSMember> findByEmailAndCustomer_Id(String email, Long customerId);

    Optional<CMSMember> findByIdAndCustomer_Id(Long id, Long customerId);

    List<CMSMember> findAllByCustomer_Id(Long customerId);
}
