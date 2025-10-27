package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.AdminDTO;
import redot.redot_server.domain.admin.entity.Admin;
import redot.redot_server.domain.admin.repository.AdminRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminDTO createAdmin(AdminCreateRequest request) {
        Admin admin = adminRepository.save(
                Admin.create(request.name(), request.email(), request.profileImageUrl(), passwordEncoder.encode(request.password())));
        return new AdminDTO(admin.getId(), admin.getName(), admin.getEmail(), admin.getProfileImageUrl(),
                admin.getCreatedAt());
    }
}
