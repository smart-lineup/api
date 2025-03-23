package com.jun.smartlineup.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueReorderRequestDto {
    @NotNull
    private final Long movedQueueId;
    @NotNull
    private final Long targetQueueId;
    @NotNull
    private final Long lineId;

}
