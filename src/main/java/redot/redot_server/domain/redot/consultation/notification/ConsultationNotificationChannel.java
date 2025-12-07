package redot.redot_server.domain.redot.consultation.notification;

import redot.redot_server.domain.redot.consultation.entity.Consultation;

public interface ConsultationNotificationChannel {

    void send(Consultation consultation);
}
