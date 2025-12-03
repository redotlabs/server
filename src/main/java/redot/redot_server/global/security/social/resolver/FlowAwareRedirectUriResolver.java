package redot.redot_server.global.security.social.resolver;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.security.social.SocialAuthorizationEndpoints;
import redot.redot_server.global.security.social.config.AuthRedirectProperties;
import redot.redot_server.global.security.social.config.FlowRedirect;
import redot.redot_server.global.security.social.model.FlowResolver;

@Component
public class FlowAwareRedirectUriResolver implements OAuth2AuthorizationRequestResolver {

    private final List<OAuth2AuthorizationRequestResolver> delegates;
    private final AuthRedirectProperties authRedirectProperties;

    public FlowAwareRedirectUriResolver(ClientRegistrationRepository clientRegistrationRepository,
                                        AuthRedirectProperties authRedirectProperties) {
        this.delegates = SocialAuthorizationEndpoints.SUPPORTED_AUTHORIZATION_BASE_URIS.stream()
                .map(base -> new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, base))
                .collect(Collectors.toList());
        this.authRedirectProperties = authRedirectProperties;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest original = resolveWithDelegates(request, null);
        return customize(original, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest original = resolveWithDelegates(request, clientRegistrationId);
        return customize(original, request);
    }

    private OAuth2AuthorizationRequest resolveWithDelegates(HttpServletRequest request, String clientRegistrationId) {
        for (OAuth2AuthorizationRequestResolver delegate : delegates) {
            OAuth2AuthorizationRequest resolved = clientRegistrationId == null
                    ? delegate.resolve(request)
                    : delegate.resolve(request, clientRegistrationId);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
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
