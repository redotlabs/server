package redot.redot_server.domain.redot.admin.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.redot.admin.entity.Admin;

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
