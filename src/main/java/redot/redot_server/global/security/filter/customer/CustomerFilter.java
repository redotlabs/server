package redot.redot_server.global.security.filter.customer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
import redot.redot_server.global.customer.context.CustomerContextHolder;
import redot.redot_server.global.customer.util.DomainParser;

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
            Optional<String> domainOptional = DomainParser.extractDomain(request.getHeader(HttpHeaders.HOST));
            if (domainOptional.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            Long customerId = resolveCustomerId(domainOptional.get());
            if (customerId == null) {
                commenceFailure(request, response, new AuthException(AuthErrorCode.CUSTOMER_DOMAIN_NOT_FOUND));
                return;
            }

            CustomerContextHolder.set(customerId);
            filterChain.doFilter(request, response);
        } catch (AuthException authException) {
            commenceFailure(request, response, authException);
        } finally {
            CustomerContextHolder.clear();
        }
    }

    private Long resolveCustomerId(String domain) {
        if (!StringUtils.hasText(domain)) {
            return null;
        }

        Optional<Domain> domainLookup = domainRepository.findByDomainName(domain);
        if (domainLookup.isEmpty()) {
            domainLookup = domainRepository.findByCustomDomain(domain);
        }

        Domain resolvedDomain = domainLookup.orElse(null);
        if (resolvedDomain == null || Boolean.TRUE.equals(resolvedDomain.getReserved())) {
            return null;
        }

        Customer customer = resolvedDomain.getCustomer();
        if (customer == null || customer.getId() == null) {
            return null;
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
