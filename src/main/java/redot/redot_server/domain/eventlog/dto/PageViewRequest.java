package redot.redot_server.domain.eventlog.dto;

import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.eventlog.entity.DeviceType;

public record PageViewRequest(
        @NotNull DeviceType deviceType
        //String anonymousId // 비회원이면 프론트에서 UUID 만들어서 넣어주면 좋음
) {}
