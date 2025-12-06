package redot.redot_server.domain.admin.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.admin.entity.Admin;

public record AdminResponse(
        Long id,
        String name,
        String profileImageUrl,
        String email,
        LocalDateTime createdAt
) {
    public static AdminResponse from(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getProfileImageUrl(),
                admin.getEmail(),
                admin.getCreatedAt()
        );
    }
}
