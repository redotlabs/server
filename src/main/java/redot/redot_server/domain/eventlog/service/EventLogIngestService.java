package redot.redot_server.domain.eventlog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redot.redot_server.domain.eventlog.dto.PageViewCommand;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventLogIngestService {

    private final EventLogStore eventLogStore;
    private final ObjectMapper objectMapper;

    /*
        페이지 뷰 이벤트 수집
     */
    public void ingestPageView(PageViewCommand cmd) {
        try {
            String json = objectMapper.writeValueAsString(cmd);
            eventLogStore.pushPageView(cmd.redotAppId(), json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize page view event. cmd={}", cmd, e);
        }
    }
}