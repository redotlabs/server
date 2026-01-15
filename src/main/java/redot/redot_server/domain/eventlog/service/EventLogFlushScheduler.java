package redot.redot_server.domain.eventlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.eventlog.dto.PageViewCommand;
import redot.redot_server.domain.eventlog.entity.EventLogEntity;
import redot.redot_server.domain.eventlog.repository.EventLogRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventLogFlushScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final EventLogStore eventLogStore;
    private final ObjectMapper objectMapper;
    private final EventLogRepository repo;

    // 한번에 최대 몇 개 flush할지
    private static final int BATCH_SIZE = 2000;

    /*
        등록된 모든 redotAppId에 대해 Redis 버퍼에서 이벤트를 읽어와 DB에 저장
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void flushAllApps() {
        List<Long> apps = eventLogStore.getRegisteredApps();
        for (Long appId : apps) {
            flushApp(appId);
        }
    }

    /*
        한 앱에 대해 Redis 버퍼에서 이벤트를 읽어와 DB에 저장
    */
    public void flushApp(Long redotAppId) {
        String key = eventLogStore.bufferKey(redotAppId);

        List<String> items = stringRedisTemplate.opsForList().range(key, 0, BATCH_SIZE - 1);
        if (items == null || items.isEmpty()) return;

        List<EventLogEntity> entities = new ArrayList<>(items.size());
        for (String json : items) {
            try {
                PageViewCommand cmd = objectMapper.readValue(json, PageViewCommand.class);
                entities.add(EventLogEntity.pageView(
                        cmd.eventId(),
                        cmd.redotAppId(),
//                        cmd.actorType(),
//                        cmd.actorId(),
//                        cmd.anonymousId(),
                        cmd.deviceType(),
                        cmd.ip(),
                        cmd.occurredAt()
                ));
            } catch (Exception ignored) { }
        }

        // 중복(event_id unique)으로 saveAll 실패 가능 → 폴백
        try {
            repo.saveAll(entities);
        } catch (Exception e) {
            for (EventLogEntity entity : entities) {
                try { repo.save(entity); } catch (Exception ignored) {}
            }
        }

        // DB 저장 성공(중복은 무시) 후 trim
        stringRedisTemplate.opsForList().trim(key, items.size(), -1);
    }
}