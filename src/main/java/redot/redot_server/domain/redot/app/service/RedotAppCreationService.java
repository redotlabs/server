package redot.redot_server.domain.redot.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.redot.app.dto.response.RedotAppResponse;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.plan.entity.Plan;
import redot.redot_server.domain.redot.plan.exception.PlanErrorCode;
import redot.redot_server.domain.redot.plan.exception.PlanException;
import redot.redot_server.domain.redot.plan.repository.PlanRepository;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.domain.site.domain.util.SubDomainNameGenerator;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.setting.repository.SiteSettingRepository;
import redot.redot_server.domain.site.style.entity.StyleInfo;
import redot.redot_server.domain.site.style.repository.StyleInfoRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RedotAppCreationService {

    private final RedotAppRepository redotAppRepository;
    private final PlanRepository planRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;

    public RedotAppInfoResponse createWithOwner(RedotAppCreateRequest request, RedotMember owner) {
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new PlanException(PlanErrorCode.PLAN_NOT_FOUND));

        RedotApp redotApp = redotAppRepository.save(RedotApp.create(request.name(), owner, plan));
        return initializeAppAssets(request, redotApp);
    }

    public RedotAppInfoResponse createWithoutOwner(RedotAppCreateRequest request) {
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new PlanException(PlanErrorCode.PLAN_NOT_FOUND));

        RedotApp redotApp = redotAppRepository.save(RedotApp.createWithoutOwner(request.name(), plan));
        return initializeAppAssets(request, redotApp);
    }

    private RedotAppInfoResponse initializeAppAssets(RedotAppCreateRequest request, RedotApp redotApp) {
        String domainName = SubDomainNameGenerator.generateSubdomain();
        Domain domain = domainRepository.save(Domain.ofRedotApp(domainName, redotApp));

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(redotApp));

        StyleInfo styleInfo = styleInfoRepository.save(StyleInfo.create(
                request.font(),
                request.color(),
                request.theme(),
                redotApp
        ));

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(redotApp.getOwner())
        );
    }
}
