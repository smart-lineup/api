package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.*;
import com.jun.smartlineup.user.dto.CustomUserDetails;

import java.util.List;

public interface QueueService {
    void save(Queue queue);

    List<QueueResponseDto> findQueues(CustomUserDetails user, Long lineId);

    void reorder(CustomUserDetails userDetails, QueueReorderRequestDto dto);

    void changeStatus(CustomUserDetails userDetails, Long queueId, String status);

    void delete(CustomUserDetails userDetails, Long queueId);

    void attendeeInfoChange(CustomUserDetails userDetails, Long queueId, QueueAttendeeChangeRequestDto dto);

    void batchAdd(CustomUserDetails userDetails, QueueBatchAddRequestDto dto);

    void addQueueByUser(CustomUserDetails userDetails, QueueAddRequestDto dto);
}
