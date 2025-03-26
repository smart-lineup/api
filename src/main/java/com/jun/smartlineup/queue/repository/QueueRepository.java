package com.jun.smartlineup.queue.repository;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    @Query("SELECT q FROM Queue q WHERE q.line.id = :lineId AND q.line.user = :user AND q.deletedAt is null")
    List<Queue> findAllByUserAndLine_Id(@Param("user") User user, @Param("lineId") Long lineId);

    Optional<Queue> findFirstByLineAndDeletedAtIsNullOrderByIdDesc(Line line);

    @Query("SELECT q FROM Queue q WHERE q.line.user = :user AND q.id = :queueId And q.deletedAt is null")
    Optional<Queue> findByUserAndQueue_Id(@Param("user") User user, @Param("queueId") Long queueId);
}
