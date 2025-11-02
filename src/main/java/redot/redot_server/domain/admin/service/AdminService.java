package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.AdminResponse;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.common.dto.PageResponse;
import redot.redot_server.global.util.EmailUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        final String normalizedEmail = EmailUtils.normalize(request.email());

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
            return new AdminResponse(admin.getId(), admin.getName(), admin.getProfileImageUrl(), admin.getEmail(),
                    admin.getCreatedAt());
        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex);
        }
    }

    public AdminResponse getAdminInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));

        return new AdminResponse(admin.getId(), admin.getName(), admin.getProfileImageUrl(), admin.getEmail(),
                admin.getCreatedAt());
    }

    public PageResponse<AdminResponse> getAdminInfoList(Pageable pageable) {
        Page<Admin> admins = adminRepository.findAll(pageable);
        return PageResponse.from(admins.map(AdminResponse::from));
    }

    @Transactional
    public void deleteAdmin(Long currentAdminId, Long adminId) {
        if (currentAdminId.equals(adminId)) {
            throw new AuthException(AuthErrorCode.CANNOT_DELETE_OWN_ADMIN_ACCOUNT);
        }
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));
        admin.delete();
    }


    @Transactional
    public void deleteCurrentAdmin(Long id) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));
        admin.delete();
    }
}
