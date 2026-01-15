package redot.redot_server.domain.eventlog.dto;

import redot.redot_server.domain.eventlog.entity.DeviceType;

import java.time.Instant;
import java.util.UUID;

public record PageViewCommand(
        UUID eventId,
        Long redotAppId,
//        IdentityType actorType,
//        Long actorId,          // nullable
//        String anonymousId,    // nullable
        DeviceType deviceType,
        String ip,
        Instant occurredAt
) {}
