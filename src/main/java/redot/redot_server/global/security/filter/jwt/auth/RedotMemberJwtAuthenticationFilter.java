package redot.redot_server.global.security.filter.jwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.domain.redot.member.service.RedotMemberStatusValidator;
import redot.redot_server.global.jwt.provider.JwtProvider;
import redot.redot_server.global.jwt.token.TokenType;

@Component
public class RedotMemberJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    private final RedotMemberRepository redotMemberRepository;
    private final RedotMemberStatusValidator redotMemberStatusValidator;

    public RedotMemberJwtAuthenticationFilter(JwtProvider jwtProvider,
                                              AuthenticationEntryPoint authenticationEntryPoint,
                                              RedotMemberRepository redotMemberRepository,
                                              RedotMemberStatusValidator redotMemberStatusValidator) {
        super(jwtProvider, authenticationEntryPoint, TokenType.REDOT_MEMBER);
        this.redotMemberRepository = redotMemberRepository;
        this.redotMemberStatusValidator = redotMemberStatusValidator;
    }

    @Override
    protected void validateClaims(Claims claims, HttpServletRequest request) {
        Long memberId = extractSubjectId(claims);
        RedotMember member = redotMemberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        redotMemberStatusValidator.ensureActive(member);
    }

    private Long extractSubjectId(Claims claims) {
        String subject = claims.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT, ex);
        }
    }
}
