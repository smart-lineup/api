package com.jun.smartlineup.queue.dto;

import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueAddRequestDto {
    @NotNull
    private Long lineId;
    private AttendeeAddRequestDto attendee;
}
