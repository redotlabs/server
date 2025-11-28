package redot.redot_server.support.security.social.profile;

import java.util.Map;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.support.security.social.model.SocialProfile;

public interface SocialProfileExtractor {
    SocialProvider provider();

    SocialProfile extract(Map<String, Object> attributes);
}
