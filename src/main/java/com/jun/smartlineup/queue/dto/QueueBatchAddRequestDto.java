package com.jun.smartlineup.queue.dto;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.user.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QueueBatchAddRequestDto {
    @NotNull
    private Long lineId;
    @NotNull
    private List<AttendeeDto> attendees;

    @Data
    public static class AttendeeDto {
        private String name;
        private String phone;
        private String info;

        public Attendee toEntity(User user) {
            return Attendee.builder()
                    .user(user)
                    .name(name)
                    .phone(phone)
                    .info(info)
                    .build();
        }
    }
}
