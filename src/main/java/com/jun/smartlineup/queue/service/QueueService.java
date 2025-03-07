package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.user.domain.User;

public interface QueueService {
    void save(Queue queue);

    Queue findQueue(User user, Line line);

}
