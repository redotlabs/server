package redot.redot_server.global.security.social.profile;

import java.util.Map;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.global.security.social.model.SocialProfile;

public interface SocialProfileExtractor {
    SocialProvider provider();

    SocialProfile extract(Map<String, Object> attributes);
}
