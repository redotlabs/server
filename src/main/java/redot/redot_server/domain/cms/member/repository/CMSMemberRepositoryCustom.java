package redot.redot_server.domain.cms.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.cms.member.dto.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.entity.CMSMember;

public interface CMSMemberRepositoryCustom {
    Page<CMSMember> findAllBySearchCondition(Long customerId,
                                             CMSMemberSearchCondition searchCondition,
                                             Pageable pageable);
}
