package com.jun.smartlineup.queue.repository;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findQueueByUserAndLine(User user, Line line);
}
