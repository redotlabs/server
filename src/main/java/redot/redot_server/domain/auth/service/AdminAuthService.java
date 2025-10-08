package redot.redot_server.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.dto.AuthResult;
import redot.redot_server.domain.auth.dto.SignInRequest;
import redot.redot_server.global.jwt.token.TokenContext;
import redot.redot_server.global.jwt.token.TokenType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult signIn(SignInRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return authTokenService.issueTokens(
                new TokenContext(admin.getId(), TokenType.ADMIN, null, null)
        );
    }
}
