package redot.redot_server.domain.redot.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppCreateRequest;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppInfoResponse;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppResponse;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.redot.admin.entity.Domain;
import redot.redot_server.domain.redot.admin.repository.DomainRepository;
import redot.redot_server.domain.redot.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.redot.admin.util.SubDomainNameGenerator;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRedotAppService {
    private final RedotAppRepository redotAppRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;

    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request) {
        RedotApp redotApp = redotAppRepository.save(RedotApp.createWithoutOwner(request.name()));

        String domainName = SubDomainNameGenerator.generateSubdomain();
        Domain domain = domainRepository.save(Domain.ofRedotApp(domainName, redotApp));

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(redotApp));

        StyleInfo styleInfo = styleInfoRepository.save(
                StyleInfo.create(
                        request.font(),
                        request.color(),
                        request.theme(),
                        redotApp
                )
        );

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(redotApp.getOwner())
        );
    }
}
