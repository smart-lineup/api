package com.jun.smartlineup.queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueReorderRequestDto {
    @NotNull
    private Long movedQueueId;
    @NotNull
    private Long targetQueueId;
    @NotNull
    private Long lineId;
    @NotBlank
    @Pattern(regexp = "^(up|down)$", message = "direction must be 'up' or 'down'")
    private String direction;
}
