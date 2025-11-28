package redot.redot_server.support.security.social.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.entity.SocialProvider;
import redot.redot_server.domain.redot.member.service.RedotMemberService;
import redot.redot_server.support.security.social.model.FlowResolver;
import redot.redot_server.support.security.social.model.RedotOAuth2User;
import redot.redot_server.support.security.social.model.SocialLoginFlow;
import redot.redot_server.support.security.social.model.SocialProfile;
import redot.redot_server.support.security.social.profile.SocialProfileExtractor;

@Service
public class RedotOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final RedotMemberService redotMemberService;
    private final Map<SocialProvider, SocialProfileExtractor> extractorMap;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public RedotOAuth2UserService(RedotMemberService redotMemberService,
                                  List<SocialProfileExtractor> extractors) {
        this.redotMemberService = redotMemberService;
        this.extractorMap = extractors.stream()
                .collect(Collectors.toMap(SocialProfileExtractor::provider, Function.identity()));
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return createUser(registrationId, oAuth2User.getAttributes());
    }

    public RedotOAuth2User createUser(String registrationId, Map<String, Object> attributes) {
        try {
            String flowKey = FlowResolver.resolveFlow(registrationId);
            SocialProvider provider = resolveProvider(registrationId);
            SocialProfileExtractor extractor = resolveExtractor(provider);

            SocialProfile profile = extractor.extract(attributes);
            RedotMember member = redotMemberService.findOrCreateSocialMember(profile, provider);

            SocialLoginFlow flow = SocialLoginFlow.from(flowKey);
            return new RedotOAuth2User(member.getId(), flow, attributes);
        } catch (AuthException ex) {
            throw toOAuth2Exception(ex);
        }
    }

    private SocialProvider resolveProvider(String registrationId) {
        String providerKey = FlowResolver.resolveProviderKey(registrationId);
        try {
            return SocialProvider.valueOf(providerKey.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_LOGIN_PROVIDER, ex);
        }
    }

    private SocialProfileExtractor resolveExtractor(SocialProvider provider) {
        SocialProfileExtractor extractor = extractorMap.get(provider);
        if (extractor == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_LOGIN_PROVIDER);
        }
        return extractor;
    }

    private OAuth2AuthenticationException toOAuth2Exception(AuthException ex) {
        String errorCode = "AUTH_" + ex.getErrorCode().getExceptionCode();
        OAuth2Error error = new OAuth2Error(
                errorCode,
                ex.getMessage(),
                null
        );
        return new OAuth2AuthenticationException(error, ex.getMessage(), ex);
    }
}
