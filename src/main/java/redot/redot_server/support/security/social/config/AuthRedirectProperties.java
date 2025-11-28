package redot.redot_server.support.security.social.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthRedirectProperties {

    private Map<String, FlowRedirect> flows = new HashMap<>();
    private RedirectCookieProperties redirectCookie = new RedirectCookieProperties("REDOT_REDIRECT_URI", 300);
    private RedirectCookieProperties failureCookie = new RedirectCookieProperties("REDOT_FAILURE_URI", 300);

    public FlowRedirect getFlow(String flow) {
        FlowRedirect flowRedirect = flows.get(flow);
        if (flowRedirect == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_LOGIN_FLOW);
        }
        return flowRedirect;
    }

    public String validateOrDefaultSuccess(String flow, String candidate) {
        FlowRedirect flowRedirect = getFlow(flow);
        if (StringUtils.hasText(candidate) && flowRedirect.isAllowed(candidate)) {
            return candidate;
        }
        return flowRedirect.defaultSuccessRedirect();
    }

    public String validateOrDefaultFailure(String flow, String candidate) {
        FlowRedirect flowRedirect = getFlow(flow);
        if (StringUtils.hasText(candidate) && flowRedirect.isAllowed(candidate)) {
            return candidate;
        }
        return flowRedirect.defaultFailureRedirect();
    }
}
