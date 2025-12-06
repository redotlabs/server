package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.auth.dto.request.PasswordResetConfirmRequest;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;
import redot.redot_server.domain.admin.dto.response.AdminResponse;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.global.security.filter.jwt.refresh.RefreshTokenPayloadHolder;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public AuthResult signIn(HttpServletRequest request, SignInRequest signInRequest) {
        Admin admin = adminRepository.findByEmailIgnoreCase(EmailUtils.normalize(signInRequest.email()))
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

    public AdminResponse getCurrentAdminInfo(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));

        return new AdminResponse(admin.getId(), admin.getName(), admin.getProfileImageUrl(), admin.getEmail(),
                admin.getCreatedAt());
    }

    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        emailVerificationService.consumeVerifiedToken(
                EmailVerificationPurpose.REDOT_ADMIN_PASSWORD_RESET,
                normalizedEmail,
                request.verificationToken()
        );

        Admin admin = adminRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));

        admin.resetPassword(passwordEncoder.encode(request.newPassword()));
    }

}
