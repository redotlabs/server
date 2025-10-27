package redot.redot_server.domain.admin.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.AdminDTO;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminDTO createAdmin(AdminCreateRequest request) {
        final String normalizedEmail = normalizeEmail(request.email());

        if (adminRepository.existsByEmail(normalizedEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            Admin admin = adminRepository.save(
                    Admin.create(
                            request.name(),
                            normalizedEmail,
                            request.profileImageUrl(),
                            passwordEncoder.encode(request.password())
                    ));
            return new AdminDTO(admin.getId(), admin.getName(), admin.getProfileImageUrl(), admin.getEmail(),
                    admin.getCreatedAt());
        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
