package redot.redot_server.support.security.social.model;

import lombok.experimental.UtilityClass;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@UtilityClass
public class FlowResolver {

    public String resolveFlow(String registrationId) {
        int index = registrationId.lastIndexOf('-');
        if (index <= 0) {
            throw new AuthException(AuthErrorCode.INVALID_SOCIAL_LOGIN_REGISTRATION);
        }
        return registrationId.substring(0, index);
    }

    public String resolveProviderKey(String registrationId) {
        int index = registrationId.lastIndexOf('-');
        if (index <= 0 || index == registrationId.length() - 1) {
            throw new AuthException(AuthErrorCode.INVALID_SOCIAL_LOGIN_REGISTRATION);
        }
        return registrationId.substring(index + 1);
    }
}
