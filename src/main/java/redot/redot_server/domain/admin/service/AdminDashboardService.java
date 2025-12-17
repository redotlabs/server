package redot.redot_server.domain.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
        ZoneId kstZone = ZoneId.of("Asia/Seoul");
        LocalDateTime startOfTodayKst = LocalDate.now(kstZone).atStartOfDay();
        LocalDateTime startOfTodayUtc = startOfTodayKst.atZone(kstZone)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();

        long totalRedotMembers = redotMemberRepository.count();
        long redotMembersUntilYesterday = redotMemberRepository.countByCreatedAtBefore(startOfTodayUtc);

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
