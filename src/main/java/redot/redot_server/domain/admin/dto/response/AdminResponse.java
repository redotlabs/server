package redot.redot_server.domain.admin.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.global.s3.util.ImageUrlResolver;

public record AdminResponse(
        Long id,
        String name,
        String profileImageUrl,
        String email,
        LocalDateTime createdAt
) {
    public static AdminResponse from(Admin admin, ImageUrlResolver imageUrlResolver) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                imageUrlResolver.toPublicUrl(admin.getProfileImageUrl()),
                admin.getEmail(),
                admin.getCreatedAt()
        );
    }
}
