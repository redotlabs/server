package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminDTO;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayloadHolder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(HttpServletRequest request, SignInRequest signInRequest) {
        Admin admin = adminRepository.findByEmail(normalizeEmail(signInRequest.email()))
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (!passwordEncoder.matches(signInRequest.password(), admin.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_USER_INFO);
        }

        return authTokenService.issueTokens(request,
                new TokenContext(admin.getId(), TokenType.ADMIN, null, null)
        );
    }

    public AuthResult reissueToken(HttpServletRequest request) {
        RefreshTokenPayload payload = RefreshTokenPayloadHolder.get(request);

        if (payload == null) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }

        Long adminId = payload.subjectId();
        if (adminId == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        return authTokenService.issueTokens(request,
                new TokenContext(
                        adminId,
                        payload.tokenType(),
                        payload.roles(),
                        null
                ));
    }

    public AdminDTO getCurrentAdminInfo(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));

        return new AdminDTO(admin.getId(), admin.getName(), admin.getProfileImageUrl(), admin.getEmail(),
                admin.getCreatedAt());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
