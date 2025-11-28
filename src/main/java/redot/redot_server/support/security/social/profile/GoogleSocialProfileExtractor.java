package redot.redot_server.support.security.social.profile;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.security.social.model.SocialProfile;

@Component
public class GoogleSocialProfileExtractor implements SocialProfileExtractor {

    @Override
    public SocialProvider provider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    public SocialProfile extract(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.getOrDefault("name", attributes.get("given_name"));
        String picture = (String) attributes.get("picture");
        String providerId = (String) attributes.get("sub");

        if (!StringUtils.hasText(email) || !StringUtils.hasText(providerId)) {
            throw new AuthException(AuthErrorCode.INVALID_SOCIAL_PROFILE);
        }

        return new SocialProfile(email, name, picture, providerId);
    }
}
