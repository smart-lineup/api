package com.jun.smartlineup.attendee.dao;

import com.jun.smartlineup.queue.domain.QueueStatus;

public record QueueDao(
        Long id,
        Long nextId,
        Long prevId,
        QueueStatus status,
        String name,
        String phone
){}
