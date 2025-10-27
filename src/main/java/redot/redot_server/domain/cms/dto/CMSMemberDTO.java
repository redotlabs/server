package redot.redot_server.domain.cms.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.entity.CMSMemberRole;

public record CMSMemberDTO(
        Long customerId,
        Long id,
        String name,
        String email,
        String profileImageUrl,
        CMSMemberRole role,
        LocalDateTime createdAt
) {
}
