package redot.redot_server.domain.cms.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.member.entity.CMSMember;

public interface CMSMemberRepository extends JpaRepository<CMSMember, Long>, CMSMemberRepositoryCustom {
    Optional<CMSMember> findByEmailAndCustomer_Id(String email, Long customerId);

    Optional<CMSMember> findByIdAndCustomer_Id(Long id, Long customerId);

    List<CMSMember> findAllByCustomer_Id(Long customerId);

    @Query(value = " SELECT * FROM cms_members WHERE id = :id AND customer_id = :customerId", nativeQuery = true)
    Optional<CMSMember> findByIdAndCustomer_IdIncludingDeleted(
            @Param("id") Long id,
            @Param("customerId") Long customerId);

}
