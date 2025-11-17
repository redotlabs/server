package redot.redot_server.support.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberStatus;
import redot.redot_server.domain.cms.customer.entity.Customer;
import redot.redot_server.domain.cms.customer.entity.CustomerStatus;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.customer.repository.CustomerRepository;
import redot.redot_server.support.customer.context.CustomerContextHolder;
import redot.redot_server.support.jwt.cookie.CookieProvider;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenType;

@Component
public class CustomerRefreshTokenFilter extends AbstractRefreshTokenFilter {
    private final CustomerRepository customerRepository;
    private final CMSMemberRepository cmsMemberRepository;
    public CustomerRefreshTokenFilter(JwtProvider jwtProvider,
                                      CookieProvider cookieProvider,
                                      AuthenticationEntryPoint authenticationEntryPoint,
                                      CustomerRepository customerRepository,
                                      CMSMemberRepository cmsMemberRepository) {
        super(jwtProvider, cookieProvider, authenticationEntryPoint);
        this.customerRepository = customerRepository;
        this.cmsMemberRepository = cmsMemberRepository;
    }

    @Override
    protected TokenType requiredTokenType() {
        return TokenType.CMS;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long cmsMemberId = extractSubjectId(claims);

        Long contextCustomerId = CustomerContextHolder.get();
        Long tokenCustomerId = extractCustomerId(claims.get("customer_id"));

        if (contextCustomerId == null) {
            throw new AuthException(AuthErrorCode.CUSTOMER_CONTEXT_REQUIRED);
        }
        if (tokenCustomerId == null || !contextCustomerId.equals(tokenCustomerId)) {
            throw new AuthException(AuthErrorCode.CUSTOMER_TOKEN_MISMATCH);
        }

        Customer customer = customerRepository.findById(tokenCustomerId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.CUSTOMER_INACTIVE);
        }

        CMSMember cmsMember = cmsMemberRepository.findByIdAndCustomer_IdIncludingDeleted(cmsMemberId, tokenCustomerId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (cmsMember.getStatus() == CMSMemberStatus.DELETED) {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }
    }
}
