package redot.redot_server.domain.eventlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redot.redot_server.domain.eventlog.dto.PageViewCommand;
import redot.redot_server.domain.eventlog.entity.DeviceType;
import redot.redot_server.domain.eventlog.entity.EventLogEntity;
import redot.redot_server.domain.eventlog.repository.EventLogRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventLogFlushSchedulerTest {

    private StringRedisTemplate template;
    private EventLogStore store;
    private ObjectMapper om;
    private EventLogRepository repo;

    @SuppressWarnings("unchecked")
    private ListOperations<String, String> listOps;

    private EventLogFlushScheduler scheduler;

    /*
        테스트마다 목 객체 초기화 및 스케줄러 인스턴스 생성
     */
    @BeforeEach
    void setUp() {
        template = mock(StringRedisTemplate.class);
        store = mock(EventLogStore.class);
        om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        repo = mock(EventLogRepository.class);

        listOps = mock(ListOperations.class);

        when(template.opsForList()).thenReturn(listOps);

        scheduler = new EventLogFlushScheduler(template, store, om, repo);
    }

    /*
        flushApp가 Redis에서 읽어와 DB에 저장하고, 버퍼를 잘 트림하는지 검증
     */
    @Test
    void flushApp_reads_from_redis_saves_to_db_and_trims_buffer() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));
        PageViewCommand c2 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.DESKTOP, "127.0.0.2",
                Instant.parse("2026-01-14T12:01:00Z"));

        String j1 = om.writeValueAsString(c1);
        String j2 = om.writeValueAsString(c2);

        when(listOps.range(key, 0, 2000 - 1)).thenReturn(List.of(j1, j2));

        // when
        scheduler.flushApp(appId);

        // then
        ArgumentCaptor<List<EventLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(repo, times(1)).saveAll(captor.capture());

        List<EventLogEntity> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        // trim: 앞에서 읽은 개수만큼 제거
        verify(listOps, times(1)).trim(key, 2, -1);
    }

    /*
        flushApp에서 saveAll이 실패하면 save 각각 호출하는 폴백 로직 검증
     */
    @Test
    void flushApp_when_saveAll_fails_fallbacks_to_save_each_and_still_trims() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));
        PageViewCommand c2 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.DESKTOP, "127.0.0.2",
                Instant.parse("2026-01-14T12:01:00Z"));

        String j1 = om.writeValueAsString(c1);
        String j2 = om.writeValueAsString(c2);

        when(listOps.range(key, 0, 2000 - 1)).thenReturn(List.of(j1, j2));

        doThrow(new DataIntegrityViolationException("dup"))
                .when(repo).saveAll(anyList());

        // when
        scheduler.flushApp(appId);

        // then
        verify(repo, times(1)).saveAll(anyList());
        verify(repo, times(2)).save(any(EventLogEntity.class));

        verify(listOps, times(1)).trim(key, 2, -1);
    }

    /*
        flushApp에서 Redis에 아이템이 없으면 아무 동작도 하지 않는지 검증
     */
    @Test
    void flushApp_when_no_items_does_nothing() {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        when(listOps.range(key, 0, 2000 - 1)).thenReturn(List.of());

        // when
        scheduler.flushApp(appId);

        // then
        verifyNoInteractions(repo);
        verify(listOps, never()).trim(anyString(), anyLong(), anyLong());
    }
}