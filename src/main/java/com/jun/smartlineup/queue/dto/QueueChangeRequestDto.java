package com.jun.smartlineup.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueChangeRequestDto {
    @NotNull
    private final Long queueId;
    @NotNull
    private final Long lineId;
    @NotNull
    private final Long startOrderNo;
    @NotNull
    private final Long reachOrderNo;
}
