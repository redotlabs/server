package redot.redot_server.support.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberStatus;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.redotapp.entity.RedotAppStatus;
import redot.redot_server.domain.cms.member.repository.CMSMemberRepository;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.support.redotapp.context.RedotAppContextHolder;
import redot.redot_server.support.jwt.cookie.CookieProvider;
import redot.redot_server.support.jwt.provider.JwtProvider;
import redot.redot_server.support.jwt.token.TokenType;

@Component
public class RedotAppRefreshTokenFilter extends AbstractRefreshTokenFilter {
    private final RedotAppRepository redotAppRepository;
    private final CMSMemberRepository cmsMemberRepository;
    public RedotAppRefreshTokenFilter(JwtProvider jwtProvider,
                                      CookieProvider cookieProvider,
                                      AuthenticationEntryPoint authenticationEntryPoint,
                                      RedotAppRepository redotAppRepository,
                                      CMSMemberRepository cmsMemberRepository) {
        super(jwtProvider, cookieProvider, authenticationEntryPoint);
        this.redotAppRepository = redotAppRepository;
        this.cmsMemberRepository = cmsMemberRepository;
    }

    @Override
    protected TokenType requiredTokenType() {
        return TokenType.CMS;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long cmsMemberId = extractSubjectId(claims);

        Long contextRedotAppId = RedotAppContextHolder.get();
        Long tokenRedotAppId = extractRedotAppId(claims.get("redot_app_id"));

        if (contextRedotAppId == null) {
            throw new AuthException(AuthErrorCode.REDOT_APP_CONTEXT_REQUIRED);
        }
        if (tokenRedotAppId == null || !contextRedotAppId.equals(tokenRedotAppId)) {
            throw new AuthException(AuthErrorCode.REDOT_APP_TOKEN_MISMATCH);
        }

        RedotApp redotApp = redotAppRepository.findById(tokenRedotAppId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (redotApp.getStatus() != RedotAppStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.REDOT_APP_INACTIVE);
        }

        CMSMember cmsMember = cmsMemberRepository.findByIdAndRedotApp_IdIncludingDeleted(cmsMemberId, tokenRedotAppId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_USER_INFO));

        if (cmsMember.getStatus() == CMSMemberStatus.DELETED) {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }
    }
}
