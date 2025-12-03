package redot.redot_server.domain.cms.redotapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.entity.Domain;
import redot.redot_server.domain.redot.admin.exception.DomainErrorCode;
import redot.redot_server.domain.redot.admin.exception.DomainException;
import redot.redot_server.domain.redot.admin.repository.DomainRepository;
import redot.redot_server.domain.redot.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.redot.admin.util.SubDomainNameGenerator;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.plan.entity.Plan;
import redot.redot_server.domain.cms.plan.exception.PlanErrorCode;
import redot.redot_server.domain.cms.plan.exception.PlanException;
import redot.redot_server.domain.cms.plan.repository.PlanRepository;
import redot.redot_server.domain.cms.redotapp.dto.request.RedotAppCreateManagerRequest;
import redot.redot_server.domain.cms.redotapp.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.cms.redotapp.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.cms.redotapp.dto.response.RedotAppResponse;
import redot.redot_server.domain.cms.site.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.response.StyleInfoResponse;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppErrorCode;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppException;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.domain.cms.site.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.site.exception.SiteSettingException;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;
import redot.redot_server.domain.cms.style.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.style.exception.StyleInfoException;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.global.common.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RedotAppService {

    private final RedotAppRepository redotAppRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final RedotMemberRepository redotMemberRepository;
    private final CMSMemberRepository cmsMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanRepository planRepository;
    
    public RedotAppInfoResponse getRedotAppInfo(Long redotAppId) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId).orElseThrow(
                () -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND)
        );

        Domain domain = domainRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotAppId)
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

        RedotMember owner = redotApp.getOwner();

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(owner)
        );
    }

    public PageResponse<RedotAppInfoResponse> getRedotAppList(Long ownerId, Pageable pageable) {
        Page<RedotApp> redotApps = redotAppRepository.findByOwnerId(ownerId, pageable);

        Page<RedotAppInfoResponse> responsePage = redotApps.map(redotApp -> {
            Domain domain = domainRepository.findByRedotAppId(redotApp.getId())
                    .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

            StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId())
                    .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

            SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotApp.getId())
                    .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

            return new RedotAppInfoResponse(
                    RedotAppResponse.fromEntity(redotApp),
                    SiteSettingResponse.fromEntity(siteSetting, domain),
                    StyleInfoResponse.fromEntity(styleInfo),
                    RedotMemberResponse.fromNullable(redotApp.getOwner())
            );
        });

        return PageResponse.from(responsePage);
    }

    // Admin에서도 동일한 로직이 있음. 이걸 메인으로 어드민에서 재사용하는 방법 고려 필요
    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request, Long currentUserId) {
        RedotMember owner = redotMemberRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new PlanException(PlanErrorCode.PLAN_NOT_FOUND));
        RedotApp redotApp = redotAppRepository.save(RedotApp.create(request.name(), owner, plan));

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

    @Transactional
    public void createManager(Long redotAppId, RedotAppCreateManagerRequest request, Long currentUserId) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        // 소유자 확인
        if (!redotApp.isOwner(currentUserId)) {
            throw new RedotAppException(RedotAppErrorCode.NOT_REDOT_APP_OWNER);
        }

        // 이미 초기 관리자가 생성되었는지 확인
        if (redotApp.isCreatedManager()) {
            throw new RedotAppException(RedotAppErrorCode.MANAGER_ALREADY_CREATED);
        }

        // CMS 관리자 계정 생성 (OWNER 권한)
        CMSMember manager = CMSMember.create(
                redotApp,
                request.name(),
                request.email(),
                null, // profileImageUrl
                passwordEncoder.encode(request.password()),
                CMSMemberRole.OWNER
        );

        try {
            cmsMemberRepository.save(manager);
        } catch (DataIntegrityViolationException e) {
            log.error("CMS member insert failed: {}", e.getMostSpecificCause().getMessage(), e);
            throw new RedotAppException(RedotAppErrorCode.MANAGER_ALREADY_CREATED);
        }

        // 초기 관리자 생성 완료 표시
        redotApp.markManagerCreated();
    }
}
