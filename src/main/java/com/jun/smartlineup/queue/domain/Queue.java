package com.jun.smartlineup.queue.domain;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "previous_id")
    @Setter
    private Queue previous;

    @ManyToOne
    @JoinColumn(name = "next_id")
    @Setter
    private Queue next;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Setter
    private QueueStatus status = QueueStatus.WAITING;

    @Setter
    private LocalDateTime deletedAt;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static Queue createQueue(Line line, Attendee attendee, User user) {
        return Queue.builder()
                .attendee(attendee)
                .user(user)
                .line(line)
                .build();
    }

}
