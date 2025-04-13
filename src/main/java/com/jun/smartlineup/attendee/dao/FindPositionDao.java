package com.jun.smartlineup.attendee.dao;

import com.jun.smartlineup.queue.domain.QueueStatus;

public record FindPositionDao (
        Long id,
        Long nextId,
        Long prevId,
        QueueStatus status,
        String phone
){}
