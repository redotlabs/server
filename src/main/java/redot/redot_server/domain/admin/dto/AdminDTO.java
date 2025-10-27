package redot.redot_server.domain.admin.dto;

import java.time.LocalDateTime;

public record AdminDTO(
        Long id,
        String name,
        String profileImageUrl,
        String email,
        LocalDateTime createdAt
) {
}
