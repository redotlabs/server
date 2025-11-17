package redot.redot_server.support.security.filter.jwt.refresh;

import jakarta.servlet.http.HttpServletRequest;

public final class RefreshTokenPayloadHolder {

    private RefreshTokenPayloadHolder() {}

    public static final String ATTRIBUTE_KEY = "redot.refresh.payload";

    public static void set(HttpServletRequest request, RefreshTokenPayload payload) {
        request.setAttribute(ATTRIBUTE_KEY, payload);
    }

    public static RefreshTokenPayload get(HttpServletRequest request) {
        return (RefreshTokenPayload) request.getAttribute(ATTRIBUTE_KEY);
    }
}
