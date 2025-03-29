package com.jun.smartlineup.queue.dto;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.user.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QueueBatchAddRequestDto {
    @NotNull
    private Long lineId;
    @NotNull
    private List<AttendeeAddRequestDto> attendees;
}
