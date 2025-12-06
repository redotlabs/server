package redot.redot_server.domain.redot.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public interface RedotMemberRepository extends JpaRepository<RedotMember, Long> {
    boolean existsByEmail(String email);

    Optional<RedotMember> findByEmail(String email);

    Optional<RedotMember> findBySocialProviderAndSocialProviderId(SocialProvider provider, String socialProviderId);
}
