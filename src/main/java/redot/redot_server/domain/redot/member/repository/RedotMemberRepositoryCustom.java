package redot.redot_server.domain.redot.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.admin.dto.AdminRedotMemberSearchCondition;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberProjection;

public interface RedotMemberRepositoryCustom {
    Page<AdminRedotMemberProjection> searchAdminMembers(AdminRedotMemberSearchCondition condition, Pageable pageable);
}
