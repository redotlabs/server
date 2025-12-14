package redot.redot_server.global.security.filter.jwt.refresh;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.domain.redot.member.service.RedotMemberStatusValidator;
import redot.redot_server.global.jwt.cookie.CookieProvider;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@Component
public class RedotMemberRefreshTokenFilter extends AbstractRefreshTokenFilter {

    private final RedotMemberRepository redotMemberRepository;
    private final RedotMemberStatusValidator redotMemberStatusValidator;

    public RedotMemberRefreshTokenFilter(JwtProvider jwtProvider,
                                         CookieProvider cookieProvider,
                                         AuthenticationEntryPoint authenticationEntryPoint,
                                         RedotMemberRepository redotMemberRepository,
                                         RedotMemberStatusValidator redotMemberStatusValidator) {
        super(jwtProvider, cookieProvider, authenticationEntryPoint);
        this.redotMemberRepository = redotMemberRepository;
        this.redotMemberStatusValidator = redotMemberStatusValidator;
    }

    @Override
    protected TokenType requiredTokenType() {
        return TokenType.REDOT_MEMBER;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long memberId = extractSubjectId(claims);
        RedotMember member = redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        redotMemberStatusValidator.ensureActive(member);
    }
}
