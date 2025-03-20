package com.jun.smartlineup.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueChangeRequestDto {
    @NotNull
    private final Long queue_id1;
    @NotNull
    private final Long queue_id2;
    @NotNull
    private final Long line_id;
}
