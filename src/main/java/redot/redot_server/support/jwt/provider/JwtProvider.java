package redot.redot_server.support.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;

@Component
public class JwtProvider {
    private final Key key;

    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long id, String type, List<String> roles, Long customerId) {
        return buildToken(id, type, roles, customerId, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Long id, String type, List<String> roles, Long customerId) {
        return buildToken(id, type, roles, customerId, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Long id, String type, List<String> roles, Long customerId, Duration validity) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", type)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(validity)))
                .signWith(key, SignatureAlgorithm.HS256);

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        if (customerId != null) {
            builder.claim("customer_id", customerId);
        }

        return builder.compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED, e);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        } catch (SignatureException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.EMPTY_TOKEN, e);
        }
    }

    public Duration getAccessTokenExpiration() {
        return ACCESS_TOKEN_EXPIRATION;
    }

    public Duration getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}
