package redot.redot_server.domain.cms.menu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.menu.dto.response.CmsMenuResponse;
import redot.redot_server.domain.cms.menu.service.CmsMenuService;
import redot.redot_server.domain.cms.redotapp.service.RedotAppService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@Tag(name = "CMS Menu", description = "CMS 메뉴 관리 API")
@RestController
@RequestMapping("/api/v1/app/cms/menus")
@RequiredArgsConstructor
public class CmsMenuController {

    private final CmsMenuService cmsMenuService;
    private final RedotAppService redotAppService;

    @GetMapping
    public List<CmsMenuResponse> getMenus(@CurrentRedotApp Long redotAppId) {
        Long planId = redotAppService.getRedotAppInfo(redotAppId).redotApp().planId();
        return cmsMenuService.getMenusByPlanId(planId);
    }
}

