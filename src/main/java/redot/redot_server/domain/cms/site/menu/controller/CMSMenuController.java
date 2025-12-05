package redot.redot_server.domain.cms.site.menu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.site.menu.dto.response.CMSMenuResponse;
import redot.redot_server.domain.cms.site.menu.service.CMSMenuService;
import redot.redot_server.domain.redot.app.service.RedotAppService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@Tag(name = "CMS Menu", description = "CMS 메뉴 관리 API")
@RestController
@RequestMapping("/api/v1/app/cms/menus")
@RequiredArgsConstructor
public class CMSMenuController {

    private final CMSMenuService cmsMenuService;
    private final RedotAppService redotAppService;

    @GetMapping
    public List<CMSMenuResponse> getMenus(@CurrentRedotApp Long redotAppId) {
        Long planId = redotAppService.getRedotAppInfo(redotAppId).redotApp().planId();
        return cmsMenuService.getMenusByPlanId(planId);
    }
}

