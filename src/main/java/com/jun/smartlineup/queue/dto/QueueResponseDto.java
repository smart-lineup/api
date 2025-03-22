package com.jun.smartlineup.queue.dto;

import com.jun.smartlineup.attendee.dto.AttendeeResponseDto;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.domain.QueueStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QueueResponseDto {
    private Long id;
    private AttendeeResponseDto attendee;
    private Long orderNo;
    private QueueStatus status;
    private LocalDateTime createdAt;


    public static QueueResponseDto fromEntity(Queue queue) {
        return QueueResponseDto.builder()
                .id(queue.getId())
                .attendee(AttendeeResponseDto.fromDto(queue.getAttendee()))
                .orderNo(queue.getOrderNo())
                .status(queue.getStatus())
                .createdAt(queue.getCreatedAt())
                .build();
    }
}
