package redot.redot_server.domain.eventlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EventLogStore {

    // 10분 버퍼 (스케줄러가 10분마다 비우는 구조)
    private static final Duration BUFFER_TTL = Duration.ofMinutes(20);

    // app 단위 버퍼 키: event-log:buffer:{appId}
    private static final String BUFFER_KEY = "event-log:buffer:%s";

    // flush 대상 앱 목록을 보관하는 set
    private static final String APPS_KEY = "event-log:apps";

    private final StringRedisTemplate stringRedisTemplate;

    /*
        페이지 뷰 이벤트를 Redis 버퍼에 저장
     */
    public void pushPageView(Long redotAppId, String payloadJson) {
        registerApp(redotAppId);

        String key = bufferKey(redotAppId);
        stringRedisTemplate.opsForList().rightPush(key, payloadJson);
        stringRedisTemplate.expire(key, BUFFER_TTL);
    }

    /*
        등록된 모든 앱 ID 조회
     */
    public List<Long> getRegisteredApps() {
        Set<String> members = stringRedisTemplate.opsForSet().members(APPS_KEY);
        if (members == null || members.isEmpty()) return List.of();
        return members.stream().map(Long::valueOf).toList();
    }

    /*
        앱 ID 등록/해제
     */
    public void registerApp(Long redotAppId) {
        stringRedisTemplate.opsForSet().add(APPS_KEY, String.valueOf(redotAppId));
    }

    /*
        앱 ID 등록 해제
     */
    public void unregisterApp(Long redotAppId) {
        stringRedisTemplate.opsForSet().remove(APPS_KEY, String.valueOf(redotAppId));
        // 버퍼도 같이 정리하고 싶으면:
        // stringRedisTemplate.delete(bufferKey(redotAppId));
    }

    /*
        특정 앱의 버퍼에 쌓여있는 이벤트 수 조회
     */
    public long size(Long redotAppId) {
        Long size = stringRedisTemplate.opsForList().size(bufferKey(redotAppId));
        return size == null ? 0 : size;
    }

    /*
        특정 앱의 버퍼 키 생성
     */
    public String bufferKey(Long redotAppId) {
        return BUFFER_KEY.formatted(redotAppId);
    }
}