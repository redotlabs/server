package redot.redot_server.global.security.social.matcher;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Lightweight replacement for deprecated AntPathRequestMatcher that still exposes URI variables.
 */
public class PathTemplateRequestMatcher implements RequestMatcher {

    private static final UrlPathHelper PATH_HELPER = new UrlPathHelper();

    private final PathPattern pathPattern;

    public PathTemplateRequestMatcher(String pattern) {
        PathPatternParser parser = new PathPatternParser();
        this.pathPattern = parser.parse(pattern);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return matchInfo(request) != null;
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        PathPattern.PathMatchInfo info = matchInfo(request);
        if (info == null) {
            return MatchResult.notMatch();
        }
        Map<String, String> variables = info.getUriVariables();
        return MatchResult.match(variables != null ? variables : Collections.emptyMap());
    }

    private PathPattern.PathMatchInfo matchInfo(HttpServletRequest request) {
        String path = normalizePath(extractPathWithinApplication(request));
        PathContainer container = PathContainer.parsePath(path);
        return pathPattern.matchAndExtract(container);
    }

    private String normalizePath(String path) {
        if (path == null || path.length() <= 1) {
            return path == null ? "" : path;
        }
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String extractPathWithinApplication(HttpServletRequest request) {
        if (ServletRequestPathUtils.hasParsedRequestPath(request)) {
            return ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication().value();
        }
        return PATH_HELPER.getPathWithinApplication(request);
    }
}
