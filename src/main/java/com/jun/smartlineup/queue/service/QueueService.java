package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.QueueChangeRequestDto;
import com.jun.smartlineup.user.dto.CustomUserDetails;

import java.util.List;

public interface QueueService {
    void save(Queue queue);

    List<Queue> findQueues(CustomUserDetails user, Long lineId);

    void addFromAttendee(Line line, Attendee attendee);

    void changeOrder(CustomUserDetails userDetails, QueueChangeRequestDto dto);
}
