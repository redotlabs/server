package redot.redot_server.domain.eventlog.entity;

import jakarta.persistence.*;
import redot.redot_server.global.common.entity.BaseTimeEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_logs",
        indexes = {
                @Index(name = "idx_event_logs_app_time", columnList = "redot_app_id, occurred_at"),
                @Index(name = "idx_event_logs_type_time", columnList = "type, occurred_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_logs_event_id", columnNames = "event_id")
        }
)
public class EventLogEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    // UUID를 varchar로 저장 (중복 방지 위함)
    @Column(name = "event_id", nullable = false, updatable = false, length = 36)
    private String eventId; // 이벤트 고유 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EventType type; // 이벤트 유형

    @Column(name = "redot_app_id", nullable = false)
    private Long redotAppId; // 리닷 앱 ID

//    @Enumerated(EnumType.STRING)
//    @Column(name = "actor_type", nullable = false, length = 32)
//    private IdentityType actorType;
//
//    @Column(name = "actor_id")
//    private Long actorId;
//
//    @Column(name = "anonymous_id", length = 64)
//    private String anonymousId;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 16)
    private DeviceType deviceType; // 디바이스 유형

    @Column(nullable = false, length = 64)
    private String ip; // IP 주소

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt; // 이벤트 발생 시각

    protected EventLogEntity() {}

    public static EventLogEntity pageView(UUID eventId, Long redotAppId, DeviceType deviceType, String ip, Instant occurredAt) {
        EventLogEntity e = new EventLogEntity();
        e.eventId = eventId.toString();
        e.type = EventType.PAGE_VIEW;
        e.redotAppId = redotAppId;
//        e.actorType = actorType;
//        e.actorId = actorId;
//        e.anonymousId = anonymousId;
        e.deviceType = deviceType;
        e.ip = ip;
        e.occurredAt = occurredAt;
        return e;
    }
}
