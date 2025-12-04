package redot.redot_server.domain.redot.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.dto.request.AdminCreateRequest;
import redot.redot_server.domain.redot.admin.dto.request.AdminResetPasswordRequest;
import redot.redot_server.domain.redot.admin.dto.response.AdminResponse;
import redot.redot_server.domain.redot.admin.dto.request.AdminUpdateRequest;
import redot.redot_server.domain.redot.admin.entity.Admin;
import redot.redot_server.domain.redot.admin.repository.AdminRepository;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.global.util.dto.response.PageResponse;
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
            return AdminResponse.from(admin);
        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex);
        }
    }

    public AdminResponse getAdminInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));

        return AdminResponse.from(admin);
    }

    public PageResponse<AdminResponse> getAdminInfoList(Pageable pageable) {
        Page<Admin> admins = adminRepository.findAll(pageable);
        return PageResponse.from(admins.map(AdminResponse::from));
    }

    @Transactional
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest request) {
        final String normalizedEmail = EmailUtils.normalize(request.email());

        try {
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));
            if (adminRepository.existsByEmail(normalizedEmail) && !normalizedEmail.equals(admin.getEmail())) {
                throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
            }

            admin.update(request.name(), normalizedEmail, request.profileImageUrl());

            return AdminResponse.from(admin);
        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex);
        }
    }

    @Transactional
    public void resetAdminPassword(Long adminId, AdminResetPasswordRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ADMIN_NOT_FOUND));
        admin.resetPassword(passwordEncoder.encode(request.password()));
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
