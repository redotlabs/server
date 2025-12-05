package redot.redot_server.domain.redot.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.redot.app.service.RedotAppCreationService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRedotAppService {
    private final RedotAppCreationService redotAppCreationService;

    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request) {
        return redotAppCreationService.createWithoutOwner(request);
    }
}
