package redot.redot_server.global.security.social;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

@UtilityClass
public class SocialAuthorizationEndpoints {

    public static final String API_AUTHORIZATION_BASE_URI = "/api/v1/oauth2/authorization";
    public static final String LEGACY_AUTHORIZATION_BASE_URI =
            OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

    public static final List<String> SUPPORTED_AUTHORIZATION_BASE_URIS = List.of(
            API_AUTHORIZATION_BASE_URI,
            LEGACY_AUTHORIZATION_BASE_URI
    );
}
