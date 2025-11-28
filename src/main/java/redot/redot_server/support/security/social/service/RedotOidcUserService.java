package redot.redot_server.support.security.social.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.stereotype.Service;
import redot.redot_server.support.security.social.model.RedotOAuth2User;
import redot.redot_server.support.security.social.model.SocialLoginFlow;


@Service
public class RedotOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final RedotOAuth2UserService redotOAuth2UserService;

    public RedotOidcUserService(RedotOAuth2UserService redotOAuth2UserService) {
        this.redotOAuth2UserService = redotOAuth2UserService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);
        RedotOAuth2User baseUser = redotOAuth2UserService.createUser(
                userRequest.getClientRegistration().getRegistrationId(),
                oidcUser.getAttributes()
        );
        return new RedotOAuth2User(
                baseUser.getMemberId(),
                SocialLoginFlow.from(baseUser.getFlowKey()),
                oidcUser.getAttributes(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }
}
