package com.jun.smartlineup.attendee.domain;

import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    //010-0000-0000
    @Size(min = 13, max = 13)
    private String phone;

    @Lob
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private String info= "{}";

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static Attendee fromDto(User user, AttendeeAddRequestDto dto) {
        return Attendee.builder()
                .user(user)
                .name(dto.getName())
                .phone(dto.getPhone())
                .build();
    }
}
