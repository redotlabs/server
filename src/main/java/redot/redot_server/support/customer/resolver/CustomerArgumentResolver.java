package redot.redot_server.support.customer.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.customer.context.CustomerContextHolder;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;

@Component
@RequiredArgsConstructor
public class CustomerArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentCustomer.class)
                && (Long.class.equals(parameter.getParameterType())
                || long.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Long customerId = CustomerContextHolder.get();
        if (customerId == null) {
            throw new AuthException(AuthErrorCode.CUSTOMER_CONTEXT_REQUIRED);
        }

        return customerId;
    }
}
