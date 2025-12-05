package redot.redot_server.domain.auth.service;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redot.redot_server.domain.auth.model.EmailVerificationPurpose;

@Component
@RequiredArgsConstructor
public class EmailVerificationStore {

    private static final String CODE_KEY = "email-verification:code:%s:%s";
    private static final String COOLDOWN_KEY = "email-verification:cooldown:%s:%s";
    private static final String TOKEN_KEY = "email-verification:token:%s:%s";

    private final StringRedisTemplate stringRedisTemplate;

    public void saveCode(EmailVerificationPurpose purpose, String email, String code, Duration ttl) {
        stringRedisTemplate.opsForValue().set(codeKey(purpose, email), code, ttl);
    }

    public Optional<String> getCode(EmailVerificationPurpose purpose, String email) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(codeKey(purpose, email)));
    }

    public void deleteCode(EmailVerificationPurpose purpose, String email) {
        stringRedisTemplate.delete(codeKey(purpose, email));
    }

    public boolean hasCooldown(EmailVerificationPurpose purpose, String email) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(cooldownKey(purpose, email)));
    }

    public void saveCooldown(EmailVerificationPurpose purpose, String email, Duration ttl) {
        stringRedisTemplate.opsForValue().set(cooldownKey(purpose, email), "cooldown", ttl);
    }

    public void saveToken(EmailVerificationPurpose purpose, String token, String email, Duration ttl) {
        stringRedisTemplate.opsForValue().set(tokenKey(purpose, token), email, ttl);
    }

    public Optional<String> getEmailByToken(EmailVerificationPurpose purpose, String token) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(tokenKey(purpose, token)));
    }

    public void deleteToken(EmailVerificationPurpose purpose, String token) {
        stringRedisTemplate.delete(tokenKey(purpose, token));
    }

    private String codeKey(EmailVerificationPurpose purpose, String email) {
        return CODE_KEY.formatted(purpose.name(), email);
    }

    private String cooldownKey(EmailVerificationPurpose purpose, String email) {
        return COOLDOWN_KEY.formatted(purpose.name(), email);
    }

    private String tokenKey(EmailVerificationPurpose purpose, String token) {
        return TOKEN_KEY.formatted(purpose.name(), token);
    }
}
