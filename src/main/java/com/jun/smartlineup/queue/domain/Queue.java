package com.jun.smartlineup.queue.domain;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private QueueStatus status = QueueStatus.WAITING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static Queue createQueue(Line line, Attendee attendee) {
        return Queue.builder()
                .attendee(attendee)
                .line(line)
                .build();
    }


}
