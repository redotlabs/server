package redot.redot_server.global.security.social.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import redot.redot_server.global.jwt.token.TokenType;

@Getter
public class RedotOAuth2User implements OidcUser {

    private final Long memberId;
    private final String flowKey;
    private final TokenType tokenType;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public RedotOAuth2User(Long memberId, SocialLoginFlow flow, Map<String, Object> attributes) {
        this(memberId, flow, attributes, null, null);
    }

    public RedotOAuth2User(Long memberId,
                           SocialLoginFlow flow,
                           Map<String, Object> attributes,
                           OidcIdToken idToken,
                           OidcUserInfo userInfo) {
        this.memberId = memberId;
        this.flowKey = flow.getFlowKey();
        this.tokenType = flow.getTokenType();
        this.attributes = attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (tokenType.getDefaultRoles().isEmpty()) {
            return Collections.emptyList();
        }
        return tokenType.getDefaultRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }

    @Override
    public Map<String, Object> getClaims() {
        if (idToken != null) {
            return idToken.getClaims();
        }
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
