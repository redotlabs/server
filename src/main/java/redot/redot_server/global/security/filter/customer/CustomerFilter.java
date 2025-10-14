package redot.redot_server.global.security.filter.customer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.entity.CustomerStatus;
import redot.redot_server.global.customer.context.CustomerContextHolder;

@Component
@RequiredArgsConstructor
public class CustomerFilter extends OncePerRequestFilter {

    private final DomainRepository domainRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String AUTH_EXCEPTION_ATTR = AuthException.class.getName();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String subdomain = request.getHeader("X-Customer-Subdomain");
            if (!StringUtils.hasText(subdomain)) {
                throw new AuthException(AuthErrorCode.CUSTOMER_CONTEXT_REQUIRED);
            }

            Long customerId = resolveCustomerId(subdomain);
            if (customerId == null) {
                throw new AuthException(AuthErrorCode.CUSTOMER_DOMAIN_NOT_FOUND);
            }

            CustomerContextHolder.set(customerId);
            filterChain.doFilter(request, response);
        } catch (AuthException authException) {
            commenceFailure(request, response, authException);
        } finally {
            CustomerContextHolder.clear();
        }
    }

    private Long resolveCustomerId(String subDomain) {

        Optional<Domain> domainLookup = domainRepository.findBySubdomain(subDomain);
        Domain resolvedDomain = domainLookup.orElse(null);

        if (resolvedDomain == null || Boolean.TRUE.equals(resolvedDomain.getReserved())) {
            return null;
        }

        Customer customer = resolvedDomain.getCustomer();
        if (customer == null || customer.getId() == null) {
            return null;
        }

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.CUSTOMER_INACTIVE);
        }

        return customer.getId();
    }

    private void commenceFailure(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthException authException) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.setAttribute(AUTH_EXCEPTION_ATTR, authException);
        authenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(authException.getMessage(), authException)
        );
    }
}
