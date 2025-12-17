package redot.redot_server.domain.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.response.AdminDashboardStatsResponse;
import redot.redot_server.domain.admin.repository.AdminRepository;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.repository.ConsultationRepository;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final RedotMemberRepository redotMemberRepository;
    private final ConsultationRepository consultationRepository;
    private final AdminRepository adminRepository;

    public AdminDashboardStatsResponse getDashboardStats() {
        LocalDateTime startOfToday = LocalDate.now(ZoneId.of("Asia/Seoul")).atStartOfDay();

        long totalRedotMembers = redotMemberRepository.count();
        long redotMembersUntilYesterday = redotMemberRepository.countByCreatedAtBefore(startOfToday);

        long pendingConsultationCount = consultationRepository.countByStatus(ConsultationStatus.PENDING);
        long adminCount = adminRepository.count();

        return new AdminDashboardStatsResponse(
                totalRedotMembers,
                redotMembersUntilYesterday,
                pendingConsultationCount,
                adminCount
        );
    }
}
