package redot.redot_server.domain.eventlog.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventLogStoreTest {

    /*
        pushPageView가 앱 등록, 페이로드 푸시, TTL 설정을 제대로 하는지 검증
     */
    @Test
    void pushPageView_registers_app_and_pushes_payload_and_sets_ttl() {
        // given
        StringRedisTemplate template = mock(StringRedisTemplate.class);

        @SuppressWarnings("unchecked")
        SetOperations<String, String> setOps = mock(SetOperations.class);
        @SuppressWarnings("unchecked")
        ListOperations<String, String> listOps = mock(ListOperations.class);

        when(template.opsForSet()).thenReturn(setOps);
        when(template.opsForList()).thenReturn(listOps);

        EventLogStore store = new EventLogStore(template);

        Long appId = 10L;
        String payload = "{\"hello\":\"world\"}";

        // when
        store.pushPageView(appId, payload);

        // then
        verify(setOps, times(1)).add("event-log:apps", "10");
        verify(listOps, times(1)).rightPush("event-log:buffer:10", payload);

        // TTL은 코드에서 Duration.ofMinutes(20)
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(template, times(1)).expire(eq("event-log:buffer:10"), ttlCaptor.capture());
        assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofMinutes(20));
    }

    /*
        getRegisteredApps가 Redis에서 앱 ID 목록을 제대로 조회하는지 검증
     */
    @Test
    void getRegisteredApps_returns_long_list() {
        // given
        StringRedisTemplate template = mock(StringRedisTemplate.class);

        @SuppressWarnings("unchecked")
        SetOperations<String, String> setOps = mock(SetOperations.class);

        when(template.opsForSet()).thenReturn(setOps);
        when(setOps.members("event-log:apps")).thenReturn(Set.of("10", "20"));

        EventLogStore store = new EventLogStore(template);

        // when
        List<Long> apps = store.getRegisteredApps();

        // then
        assertThat(apps).containsExactlyInAnyOrder(10L, 20L);
    }

    /*
        size가 Redis에서 조회한 값을 제대로 반환하는지 검증
     */
    @Test
    void size_returns_0_when_null() {
        // given
        StringRedisTemplate template = mock(StringRedisTemplate.class);

        @SuppressWarnings("unchecked")
        ListOperations<String, String> listOps = mock(ListOperations.class);

        when(template.opsForList()).thenReturn(listOps);
        when(listOps.size("event-log:buffer:10")).thenReturn(null);

        EventLogStore store = new EventLogStore(template);

        // when
        long size = store.size(10L);

        // then
        assertThat(size).isEqualTo(0);
    }
}
