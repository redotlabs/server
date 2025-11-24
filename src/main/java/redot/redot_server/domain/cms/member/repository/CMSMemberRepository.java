package redot.redot_server.domain.cms.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.member.entity.CMSMember;

public interface CMSMemberRepository extends JpaRepository<CMSMember, Long>, CMSMemberRepositoryCustom {
    Optional<CMSMember> findByEmailAndRedotApp_Id(String email, Long redotAppId);

    Optional<CMSMember> findByIdAndRedotApp_Id(Long id, Long redotAppId);

    List<CMSMember> findAllByRedotApp_Id(Long redotAppId);

    @Query(value = " SELECT * FROM cms_members WHERE id = :id AND redot_app_id = :redotAppId", nativeQuery = true)
    Optional<CMSMember> findByIdAndRedotApp_IdIncludingDeleted(
            @Param("id") Long id,
            @Param("redotAppId") Long redotAppId);

}
