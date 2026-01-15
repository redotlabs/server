package redot.redot_server.domain.eventlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import redot.redot_server.domain.eventlog.dto.PageViewCommand;
import redot.redot_server.domain.eventlog.entity.DeviceType;

import java.time.Instant;
import java.util.UUID;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventLogIngestServiceTest {

    /*
        페이지 뷰 이벤트를 직렬화하여 스토어에 푸시하는지 검증
     */
    @Test
    void ingestPageView_serializes_and_pushes_to_store() {
        EventLogStore store = mock(EventLogStore.class);

        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

        EventLogIngestService service = new EventLogIngestService(store, om);

        PageViewCommand cmd = new PageViewCommand(
                UUID.randomUUID(),
                10L,
                DeviceType.MOBILE,
                "127.0.0.1",
                Instant.parse("2026-01-14T12:00:00Z")
        );

        service.ingestPageView(cmd);

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(store, times(1)).pushPageView(eq(10L), jsonCaptor.capture());

        String json = jsonCaptor.getValue();
        assertThat(json).contains("\"redotAppId\":10");
        assertThat(json).contains("\"deviceType\":\"MOBILE\"");
        assertThat(json).contains("\"ip\":\"127.0.0.1\"");
    }
}