package redot.redot_server.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.dto.response.AdminResponse;
import redot.redot_server.domain.redot.admin.entity.Admin;
import redot.redot_server.domain.redot.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.dto.response.AuthResult;
import redot.redot_server.domain.auth.dto.request.SignInRequest;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.support.jwt.token.TokenContext;
import redot.redot_server.support.jwt.token.TokenType;
import redot.redot_server.support.security.filter.jwt.refresh.RefreshTokenPayload;
import redot.redot_server.support.security.filter.jwt.refresh.RefreshTokenPayloadHolder;
import redot.redot_server.support.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(HttpServletRequest request, SignInRequest signInRequest) {
        Admin admin = adminRepository.findByEmail(EmailUtils.normalize(signInRequest.email()))
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

}
