package redot.redot_server.global.redotapp.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.redotapp.context.RedotAppContextHolder;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@Component
@RequiredArgsConstructor
public class RedotAppArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentRedotApp.class)
                && (Long.class.equals(parameter.getParameterType())
                || long.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Long redotAppId = RedotAppContextHolder.get();
        if (redotAppId == null) {
            throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
        }

        return redotAppId;
    }
}
