package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.QueueAttendeeChangeRequestDto;
import com.jun.smartlineup.queue.dto.QueueBatchAddRequestDto;
import com.jun.smartlineup.queue.dto.QueueReorderRequestDto;
import com.jun.smartlineup.user.dto.CustomUserDetails;

import java.util.List;

public interface QueueService {
    void save(Queue queue);

    List<Queue> findQueues(CustomUserDetails user, Long lineId);

    void addFromAttendee(Line line, Attendee attendee);

    void reorder(CustomUserDetails userDetails, QueueReorderRequestDto dto);

    void changeStatus(CustomUserDetails userDetails, Long queueId, String status);

    void delete(CustomUserDetails userDetails, Long queueId);

    void attendeeInfoChange(CustomUserDetails userDetails, Long queueId, QueueAttendeeChangeRequestDto dto);

    void batchAdd(CustomUserDetails userDetails, QueueBatchAddRequestDto dto);
}
