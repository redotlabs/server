package homelabtests;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import redot.redot_server.global.security.config.HomelabCorsConfig;
import static org.assertj.core.api.Assertions.*;

class HomelabCorsTests {
    @Test void explicitOriginCanSendCredentialedApiPreflight() throws Exception {
        Filter filter = new HomelabCorsConfig().homelabCorsFilter("https://frontend.example.invalid").getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest("OPTIONS", "/api/v1/auth/redot/member/sign-in");
        req.addHeader("Origin", "https://frontend.example.invalid");
        req.addHeader("Access-Control-Request-Method", "POST");
        req.addHeader("Access-Control-Request-Headers", "Content-Type,X-App-Subdomain");
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, (a, b) -> { throw new AssertionError("Preflight must be handled before API authentication"); });
        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(res.getHeader("Access-Control-Allow-Origin")).isEqualTo("https://frontend.example.invalid");
        assertThat(res.getHeader("Access-Control-Allow-Credentials")).isEqualTo("true");
    }
    @Test void unlistedOriginIsRejected() throws Exception {
        Filter filter = new HomelabCorsConfig().homelabCorsFilter("https://frontend.example.invalid").getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest("OPTIONS", "/api/v1/app");
        req.addHeader("Origin", "https://other.example.invalid");
        req.addHeader("Access-Control-Request-Method", "GET");
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, (a, b) -> {});
        assertThat(res.getStatus()).isEqualTo(403);
    }
    @Test void wildcardAndMalformedOriginsFailClosed() {
        for (String value : new String[]{"*", "https://*.example.invalid", "", "https://example.invalid/path"}) {
            assertThatThrownBy(() -> new HomelabCorsConfig().homelabCorsFilter(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
