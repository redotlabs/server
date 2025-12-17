package redot.redot_server.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.controller.docs.AdminDashboardControllerDocs;
import redot.redot_server.domain.admin.dto.response.AdminDashboardStatsResponse;
import redot.redot_server.domain.admin.service.AdminDashboardService;

@RestController
@RequestMapping("/api/v1/redot/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController implements AdminDashboardControllerDocs {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @Override
    public ResponseEntity<AdminDashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }
}
