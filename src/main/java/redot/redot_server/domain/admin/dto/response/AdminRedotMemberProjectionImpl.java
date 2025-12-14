package redot.redot_server.domain.admin.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public record AdminRedotMemberProjectionImpl(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        SocialProvider socialProvider,
        LocalDateTime createdAt,
        RedotMemberStatus status,
        Long appCount
) implements AdminRedotMemberProjection {

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @Override
    public SocialProvider getSocialProvider() {
        return socialProvider;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public RedotMemberStatus getStatus() {
        return status;
    }

    @Override
    public Long getAppCount() {
        return appCount;
    }
}
