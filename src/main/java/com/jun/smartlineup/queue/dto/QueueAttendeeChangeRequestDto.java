package com.jun.smartlineup.queue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueAttendeeChangeRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String info;
}
