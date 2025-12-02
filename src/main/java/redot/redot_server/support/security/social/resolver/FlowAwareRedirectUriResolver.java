package redot.redot_server.support.security.social.resolver;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.security.social.config.AuthRedirectProperties;
import redot.redot_server.support.security.social.config.FlowRedirect;
import redot.redot_server.support.security.social.model.FlowResolver;

@Component
public class FlowAwareRedirectUriResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegate;
    private final AuthRedirectProperties authRedirectProperties;

    public FlowAwareRedirectUriResolver(ClientRegistrationRepository clientRegistrationRepository,
                                        AuthRedirectProperties authRedirectProperties) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
        );
        this.authRedirectProperties = authRedirectProperties;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest original = delegate.resolve(request);
        return customize(original, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest original = delegate.resolve(request, clientRegistrationId);
        return customize(original, request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest original, HttpServletRequest request) {
        if (original == null) {
            return null;
        }

        String requestedRedirect = request.getParameter(OAuth2ParameterNames.REDIRECT_URI);
        if (!StringUtils.hasText(requestedRedirect)) {
            return original;
        }

        String registrationId = extractRegistrationId(original, request);
        if (!StringUtils.hasText(registrationId)) {
            return original;
        }

        FlowRedirect flowRedirect;
        try {
            String flow = FlowResolver.resolveFlow(registrationId);
            flowRedirect = authRedirectProperties.getFlow(flow);
        } catch (AuthException ex) {
            return original;
        }

        if (!flowRedirect.isAllowed(requestedRedirect)) {
            return original;
        }

        URI requestedUri;
        try {
            requestedUri = URI.create(requestedRedirect);
        } catch (IllegalArgumentException ex) {
            return original;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(original.getRedirectUri());

        if (StringUtils.hasText(requestedUri.getScheme())) {
            builder.scheme(requestedUri.getScheme());
        }

        if (StringUtils.hasText(requestedUri.getHost())) {
            builder.host(requestedUri.getHost());
        }

        if (requestedUri.getPort() >= 0) {
            builder.port(requestedUri.getPort());
        } else {
            builder.port(null);
        }

        String overridden = builder.build(true).toUriString();
        return OAuth2AuthorizationRequest.from(original)
                .redirectUri(overridden)
                .build();
    }

    private String extractRegistrationId(OAuth2AuthorizationRequest original, HttpServletRequest request) {
        Object attribute = original.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        if (attribute instanceof String attr && StringUtils.hasText(attr)) {
            return attr;
        }
        String parameter = request.getParameter(OAuth2ParameterNames.REGISTRATION_ID);
        if (StringUtils.hasText(parameter)) {
            return parameter;
        }
        return null;
    }
}
