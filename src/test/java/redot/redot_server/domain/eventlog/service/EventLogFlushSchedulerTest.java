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
        om = new ObjectMapper().registerModule(new JavaTimeModule());

        repo = mock(EventLogRepository.class);

        listOps = mock(ListOperations.class);

        when(template.opsForList()).thenReturn(listOps);

        scheduler = new EventLogFlushScheduler(template, store, om, repo);
    }

    /*
        flushApp가 Redis에서 읽어와 DB에 저장하고 버퍼를 트림하는지 검증
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
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EventLogEntity>> captor =
                (ArgumentCaptor) ArgumentCaptor.forClass(List.class);

        verify(repo, times(1)).saveAll(captor.capture());

        List<EventLogEntity> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        // saveAll 성공이면 개별 save는 없어야 함
        verify(repo, never()).save(any(EventLogEntity.class));

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
        verify(repo, never()).saveAll(anyList());
        verify(repo, never()).save(any(EventLogEntity.class));
        verify(listOps, never()).trim(anyString(), anyLong(), anyLong());
    }


    /*
        flushApp에서 saveAll이 실패하고 일부 개별 save도 실패하는 경우에도 트림은 수행되는지 검증
     */
    @Test
    void flushApp_when_saveAll_fails_and_some_save_fails_still_continues_and_trims() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));
        PageViewCommand c2 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.DESKTOP, "127.0.0.2",
                Instant.parse("2026-01-14T12:01:00Z"));

        when(listOps.range(key, 0, 2000 - 1))
                .thenReturn(List.of(om.writeValueAsString(c1), om.writeValueAsString(c2)));

        doThrow(new DataIntegrityViolationException("dup"))
                .when(repo).saveAll(anyList());

        // 첫 번째 개별 save만 실패, 두 번째는 성공(그냥 인자를 그대로 반환)
        when(repo.save(any(EventLogEntity.class)))
                .thenThrow(new DataIntegrityViolationException("dup-one"))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // when
        scheduler.flushApp(appId);

        // then
        verify(repo, times(1)).saveAll(anyList());
        verify(repo, times(2)).save(any(EventLogEntity.class)); // 실패해도 2번 시도해야 함
        verify(listOps, times(1)).trim(key, 2, -1);
    }


    @Test
    void flushApp_when_contains_invalid_json_still_trims_by_items_size() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));

        String valid = om.writeValueAsString(c1);
        String invalid = "{not-json";

        when(listOps.range(key, 0, 2000 - 1)).thenReturn(List.of(valid, invalid));

        // when
        scheduler.flushApp(appId);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EventLogEntity>> captor =
                (ArgumentCaptor) ArgumentCaptor.forClass(List.class);

        verify(repo, times(1)).saveAll(captor.capture());

        // 유효한 것만 파싱되면 1개만 저장됨
        assertThat(captor.getValue()).hasSize(1);

        // trim은 items.size() = 2 로 수행됨(현재 정책)
        verify(listOps, times(1)).trim(key, 2, -1);
    }

    /*
        flushApp에서 saveAll이 실패하고 모든 개별 save도 실패하는 경우 DLQ로 이동하는지 검증
     */
    @Test
    void flushApp_when_saveAll_fails_and_individual_save_fails_should_push_to_dlq() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));
        PageViewCommand c2 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.DESKTOP, "127.0.0.2",
                Instant.parse("2026-01-14T12:01:00Z"));

        when(listOps.range(key, 0, 2000 - 1))
                .thenReturn(List.of(om.writeValueAsString(c1), om.writeValueAsString(c2)));

        doThrow(new DataIntegrityViolationException("dup"))
                .when(repo).saveAll(anyList());

        // 개별 save 둘 다 실패시키기 (DLQ가 2번 호출되어야 함)
        when(repo.save(any(EventLogEntity.class)))
                .thenThrow(new DataIntegrityViolationException("dup-one"))
                .thenThrow(new DataIntegrityViolationException("dup-two"));

        // when
        scheduler.flushApp(appId);

        // then
        verify(repo, times(1)).saveAll(anyList());
        verify(repo, times(2)).save(any(EventLogEntity.class));

        // ✅ DLQ로 2건 이동
        verify(store, times(2)).pushDeadLetter(eq(appId), anyString(), startsWith("DB_SAVE_FAIL:"));

        // trim은 수행
        verify(listOps, times(1)).trim(key, 2, -1);
    }

    @Test
    void flushApp_when_saveAll_fails_but_individual_saves_succeed_should_not_push_to_dlq() throws Exception {
        // given
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        PageViewCommand c1 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.MOBILE, "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z"));
        PageViewCommand c2 = new PageViewCommand(UUID.randomUUID(), appId, DeviceType.DESKTOP, "127.0.0.2",
                Instant.parse("2026-01-14T12:01:00Z"));

        when(listOps.range(key, 0, 2000 - 1))
                .thenReturn(List.of(om.writeValueAsString(c1), om.writeValueAsString(c2)));

        doThrow(new DataIntegrityViolationException("dup"))
                .when(repo).saveAll(anyList());

        // 개별 save는 성공(엔티티 반환)
        when(repo.save(any(EventLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        scheduler.flushApp(appId);

        // then
        verify(repo, times(2)).save(any(EventLogEntity.class));
        verify(store, never()).pushDeadLetter(anyLong(), anyString(), anyString());
        verify(listOps, times(1)).trim(key, 2, -1);
    }

    /*
        flushApp에서 파싱 실패 시 DLQ로 이동하는지 검증
     */
    @Test
    void flushApp_when_parse_fails_should_push_to_dlq() throws Exception {
        Long appId = 10L;
        String key = "event-log:buffer:10";
        when(store.bufferKey(appId)).thenReturn(key);

        String invalid = "{not-json";
        when(listOps.range(key, 0, 2000 - 1)).thenReturn(List.of(invalid));

        scheduler.flushApp(appId);

        verify(store, times(1)).pushDeadLetter(eq(appId), eq(invalid), startsWith("PARSE_FAIL:"));
        verify(listOps, times(1)).trim(key, 1, -1);
    }


}