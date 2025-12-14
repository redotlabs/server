package redot.redot_server.domain.redot.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
import redot.redot_server.domain.site.domain.exception.DomainErrorCode;
import redot.redot_server.domain.site.domain.exception.DomainException;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.domain.site.domain.util.SubDomainNameGenerator;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.setting.repository.SiteSettingRepository;
import redot.redot_server.domain.site.style.entity.StyleInfo;
import redot.redot_server.domain.site.style.repository.StyleInfoRepository;
import redot.redot_server.global.s3.util.ImageUrlResolver;

@Service
@RequiredArgsConstructor
@Transactional
public class RedotAppCreationService {

    private final RedotAppRepository redotAppRepository;
    private final PlanRepository planRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final ImageUrlResolver imageUrlResolver;
    private static final int DOMAIN_ALLOCATION_RETRY_LIMIT = 5;

    private Domain createDomainWithRetry(RedotApp redotApp) {
        for (int attempt = 0; attempt < DOMAIN_ALLOCATION_RETRY_LIMIT; attempt++) {
            String subdomain = SubDomainNameGenerator.generateSubdomain();
            try {
                return domainRepository.save(Domain.ofRedotApp(subdomain, redotApp));
            } catch (DataIntegrityViolationException ex) {
                if (attempt == DOMAIN_ALLOCATION_RETRY_LIMIT - 1) {
                    throw new DomainException(DomainErrorCode.DOMAIN_ALLOCATION_FAILED);
                }
            }
        }
        throw new DomainException(DomainErrorCode.DOMAIN_ALLOCATION_FAILED);
    }


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
        Domain domain = createDomainWithRetry(redotApp);

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(redotApp));

        StyleInfo styleInfo = styleInfoRepository.save(StyleInfo.create(
                request.font(),
                request.color(),
                request.theme(),
                redotApp
        ));

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain, imageUrlResolver),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(redotApp.getOwner(), imageUrlResolver)
        );
    }
}
