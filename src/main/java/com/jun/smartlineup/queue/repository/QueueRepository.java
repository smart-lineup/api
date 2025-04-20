package com.jun.smartlineup.queue.repository;

import com.jun.smartlineup.attendee.dao.FindPositionDao;
import com.jun.smartlineup.attendee.dao.QueueDao;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    @Query("SELECT q FROM Queue q WHERE q.line.id = :lineId AND q.user = :user AND q.deletedAt is null")
    List<Queue> findAllByUserAndLine_Id(@Param("user") User user, @Param("lineId") Long lineId);

    @Query("""
    SELECT new com.jun.smartlineup.attendee.dao.FindPositionDao(
        q.id,
        q.next.id,
        q.previous.id,
        q.status,
        a.name,
        a.phone
    ) FROM Queue q
    left join Attendee a on a.id = q.attendee.id
    WHERE q.line.id = :lineId AND q.deletedAt is null
    """)
    List<FindPositionDao> findAllByLine_IdForAttendee(@Param("lineId") Long lineId);

    Optional<Queue> findFirstByLineAndDeletedAtIsNullOrderByIdDesc(Line line);

    @Query("SELECT q FROM Queue q WHERE q.user = :user AND q.id = :queueId And q.deletedAt is null")
    Optional<Queue> findByUserAndQueue_Id(@Param("user") User user, @Param("queueId") Long queueId);

    @Modifying
    @Query(value = """
      DELETE FROM queue
      USING line, attendee
      WHERE queue.line_id = line.line_id
        AND queue.attendee_id = attendee.attendee_id
        AND line.uuid = :uuid
        AND attendee.phone = :phone
        AND queue.status = 'WAITING'
      RETURNING attendee.phone
    """, nativeQuery = true)
    List<String> deleteAndReturnPhones(@Param("uuid") String uuid, @Param("phone") String phone);

    @Query("""
        SELECT new com.jun.smartlineup.attendee.dao.QueueDao(
            q,
            a
        ) FROM Queue q
        LEFT JOIN Attendee a on a = q.attendee
        WHERE q.line = :line
    """)
    List<QueueDao> findQueueWithAttendeeByLine(Line line);

    void deleteAllByUser(User user);

    @Query("SELECT q FROM Queue q WHERE q.attendee.id = 2")
    List<Queue> findAllByUser(User user);
}
