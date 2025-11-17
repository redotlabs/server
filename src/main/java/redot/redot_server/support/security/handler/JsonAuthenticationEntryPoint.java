package redot.redot_server.support.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.support.exception.ErrorResponse;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.security.exception.SecurityErrorCode;

@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Object attribute = request.getAttribute(AuthException.class.getName());
        ErrorResponse errorResponse;

        if (attribute instanceof AuthException domainException) {
            errorResponse = ErrorResponse.from(domainException.getErrorCode());
        } else {
            errorResponse = ErrorResponse.from(SecurityErrorCode.UNAUTHORIZED);
        }

        response.setStatus(errorResponse.statusCode());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
