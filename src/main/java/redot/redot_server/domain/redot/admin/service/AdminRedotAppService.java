package redot.redot_server.domain.redot.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.entity.Domain;
import redot.redot_server.domain.redot.admin.repository.DomainRepository;
import redot.redot_server.domain.redot.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.redot.admin.util.SubDomainNameGenerator;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppCreateRequest;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppInfoResponse;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRedotAppService {
    private final RedotAppRepository redotAppRepository;
    private final CMSMemberRepository cmsMemberRepository;
    private final RedotMemberRepository redotMemberRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request) {
        String encodedPassword = passwordEncoder.encode(request.ownerPassword());

        RedotMember redotMemberOwner = redotMemberRepository.save(RedotMember.create(
                request.ownerName(),
                request.ownerEmail(),
                encodedPassword,
                request.ownerProfileImageUrl()
        ));

        RedotApp redotApp = redotAppRepository.save(RedotApp.create(request.companyName(), redotMemberOwner));

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

        cmsMemberRepository.save(CMSMember.create(
                        redotApp,
                        request.ownerName(),
                        request.ownerEmail(),
                        request.ownerProfileImageUrl(),
                        encodedPassword,
                        CMSMemberRole.ADMIN
                )
        );

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromEntity(redotMemberOwner)
        );
    }
}
