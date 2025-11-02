package redot.redot_server.global.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.entity.AdminStatus;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.jwt.cookie.CookieProvider;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@Component
public class AdminRefreshTokenFilter extends AbstractRefreshTokenFilter {
    private final AdminRepository adminRepository;

    public AdminRefreshTokenFilter(JwtProvider jwtProvider,
                                   CookieProvider cookieProvider,
                                   AuthenticationEntryPoint authenticationEntryPoint,
                                   AdminRepository adminRepository) {
        super(jwtProvider, cookieProvider, authenticationEntryPoint);
        this.adminRepository = adminRepository;
    }

    @Override
    protected TokenType requiredTokenType() {
        return TokenType.ADMIN;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        String subject = claims.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        Long adminId;
        try {
            adminId = Long.valueOf(subject);
        } catch (NumberFormatException ex) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT, ex);
        }

        Admin admin = adminRepository.findByIdIncludingDeleted(adminId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (admin.getStatus() == AdminStatus.DELETED) {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }
    }
}
