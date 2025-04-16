package com.jun.smartlineup.attendee.dao;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.queue.domain.Queue;

public record QueueDao(
        Queue queue,
        Attendee attendee
) {}
