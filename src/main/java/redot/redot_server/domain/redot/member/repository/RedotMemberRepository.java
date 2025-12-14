package redot.redot_server.domain.redot.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public interface RedotMemberRepository extends JpaRepository<RedotMember, Long>, RedotMemberRepositoryCustom {
    boolean existsByEmail(String email);

    Optional<RedotMember> findByEmail(String email);

    Optional<RedotMember> findBySocialProviderAndSocialProviderId(SocialProvider provider, String socialProviderId);

    @Query(value = "SELECT * FROM redot_members WHERE id = :id", nativeQuery = true)
    Optional<RedotMember> findByIdIncludingDeleted(@Param("id") Long id);
}
