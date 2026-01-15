package redot.redot_server.domain.eventlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

        List<String> items = readBatch(key);
        if (items.isEmpty()) {
            eventLogStore.unregisterApp(redotAppId);
            return;
        }

        List<EventLogEntity> entities = parseToEntities(redotAppId, items);
        saveWithDlqFallback(redotAppId, entities);

        trimProcessed(key, items.size());
    }

    /*
        Redis에서 한 번에 BATCH_SIZE만큼 항목 읽기
     */
    private List<String> readBatch(String key) {
        List<String> items = stringRedisTemplate.opsForList().range(key, 0, BATCH_SIZE - 1);
        return (items == null) ? List.of() : items;
    }

    /*
        JSON 문자열 목록을 EventLogEntity 목록으로 변환
     */
    private List<EventLogEntity> parseToEntities(Long redotAppId, List<String> items) {
        List<EventLogEntity> entities = new ArrayList<>(items.size());
        for (String json : items) {
            try {
                PageViewCommand cmd = objectMapper.readValue(json, PageViewCommand.class);
                entities.add(EventLogEntity.pageView(
                        cmd.eventId(),
                        cmd.redotAppId(),
                        cmd.deviceType(),
                        cmd.ip(),
                        cmd.occurredAt()
                ));
            } catch (Exception e) {
                eventLogStore.pushDeadLetter(redotAppId, json, "PARSE_FAIL: " + e.getMessage());
            }
        }
        return entities;
    }

    /*
        여러 엔티티를 개별 저장하며 실패 시 DLQ로 이동
     */
    private void saveWithDlqFallback(Long redotAppId, List<EventLogEntity> entities) {
        if (entities.isEmpty()) return;

        try {
            repo.saveAll(entities);
        } catch (Exception e) {
            saveIndividuallyWithDlq(redotAppId, entities);
        }
    }

    /*
        여러 엔티티를 개별 저장하며 실패 시 DLQ로 이동
     */
    private void saveIndividuallyWithDlq(Long redotAppId, List<EventLogEntity> entities) {
        for (EventLogEntity entity : entities) {
            try {
                repo.save(entity);
            } catch (Exception ex) {
                eventLogStore.pushDeadLetter(
                        redotAppId,
                        safeToJson(entity),
                        "DB_SAVE_FAIL: " + ex.getMessage()
                );
            }
        }
    }

    /*
        Redis 리스트에서 앞의 processedCount개 항목 제거
     */
    private void trimProcessed(String key, int processedCount) {
        stringRedisTemplate.opsForList().trim(key, processedCount, -1);
    }

    /*
        EventLogEntity를 안전하게 JSON 문자열로 변환
     */
    private String safeToJson(EventLogEntity entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception ignored) {
            return String.valueOf(entity);
        }
    }

}