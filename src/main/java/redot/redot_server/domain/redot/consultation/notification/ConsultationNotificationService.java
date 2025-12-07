package redot.redot_server.domain.redot.consultation.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redot.redot_server.domain.redot.consultation.entity.Consultation;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationNotificationService {

    private final List<ConsultationNotificationChannel> channels;

    public void sendConsultationCreated(Consultation consultation) {
        for (ConsultationNotificationChannel channel : channels) {
            try {
                channel.send(consultation);
            } catch (Exception ex) {
                log.error("Failed to send consultation notification via {}", channel.getClass().getSimpleName(), ex);
            }
        }
    }
}
