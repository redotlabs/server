package redot.redot_server.domain.admin.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

public interface AdminRedotMemberProjection {
    Long getId();

    String getEmail();

    String getName();

    String getProfileImageUrl();

    SocialProvider getSocialProvider();

    LocalDateTime getCreatedAt();

    RedotMemberStatus getStatus();

    Long getAppCount();
}
