package redot.redot_server.domain.cms.site.menu.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.site.menu.controller.docs.CMSMenuControllerDocs;
import redot.redot_server.domain.cms.site.menu.dto.response.CMSMenuResponse;
import redot.redot_server.domain.cms.site.menu.service.CMSMenuService;
import redot.redot_server.domain.redot.app.service.RedotAppService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@RestController
@RequestMapping("/api/v1/app/cms/menus")
@RequiredArgsConstructor
public class CMSMenuController implements CMSMenuControllerDocs {

    private final CMSMenuService cmsMenuService;
    private final RedotAppService redotAppService;

    @GetMapping
    @Override
    public List<CMSMenuResponse> getMenus(@CurrentRedotApp Long redotAppId) {
        Long planId = redotAppService.getRedotAppInfo(redotAppId).redotApp().planId();
        return cmsMenuService.getMenusByPlanId(planId);
    }
}
