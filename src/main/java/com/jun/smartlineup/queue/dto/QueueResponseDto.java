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
    private Long previousId;
    private Long nextId;
    private QueueStatus status;
    private LocalDateTime createdAt;


    public static QueueResponseDto fromEntity(Queue queue) {
        QueueResponseDto dto = QueueResponseDto.builder()
                .id(queue.getId())
                .attendee(AttendeeResponseDto.fromDto(queue.getAttendee()))
                .status(queue.getStatus())
                .createdAt(queue.getCreatedAt())
                .build();
        if (queue.getPrevious() != null) {
            dto.setPreviousId(queue.getPrevious().getId());
        }
        if (queue.getNext() != null) {
            dto.setNextId(queue.getNext().getId());
        }
        return dto;
    }
}
