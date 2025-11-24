package redot.redot_server.domain.cms.style.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoUpdateRequest;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppErrorCode;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppException;
import redot.redot_server.domain.cms.style.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.style.exception.StyleInfoException;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StyleInfoService {
    private final RedotAppRepository redotAppRepository;
    private final StyleInfoRepository styleInfoRepository;


    public StyleInfoResponse getStyleInfo(Long redotAppId) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId())
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));
        return StyleInfoResponse.fromEntity(styleInfo);
    }

    @Transactional
    public StyleInfoResponse updateStyleInfo(Long redotAppId, StyleInfoUpdateRequest request) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId())
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        styleInfo.update(request.font(), request.color(), request.theme());

        return StyleInfoResponse.fromEntity(styleInfo);
    }
}
