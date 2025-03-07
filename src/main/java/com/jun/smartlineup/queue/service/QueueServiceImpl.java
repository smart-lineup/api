package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;

    @Override
    public void save(Queue queue) {
        queueRepository.save(queue);
    }

    @Override
    public Queue findQueue(User user, Line line) {
        Optional<Queue> optionalQueue = queueRepository.findQueueByUserAndLine(user, line);
        return optionalQueue.orElseThrow(() -> new IllegalArgumentException("No exist queue"));
    }




}
