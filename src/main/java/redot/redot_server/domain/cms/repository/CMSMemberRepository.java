package redot.redot_server.domain.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.CMSMember;

public interface CMSMemberRepository extends JpaRepository<CMSMember, Long> {
}
