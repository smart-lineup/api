package com.jun.smartlineup.attendee.domain;

import com.jun.smartlineup.attendee.dao.FindPositionDao;
import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Pattern(regexp = "^\\d{2,3}-\\d{4}-\\d{4}$", message = "전화번호는 02-0000-0000 또는 010-0000-0000 형식이어야 합니다.")
    private String phone;

    @Builder.Default
    private String info = "{}";

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

    public void changeInfo(String name, String phone, String info) {
        this.name = name;
        this.phone = phone;
        this.info = info;
    }

    public void pasteFromAttendee(Attendee attendee) {
        this.id = attendee.getId();
    }
    public void pasteFromDao(FindPositionDao attendee) {
        this.id = attendee.id();
    }
}
