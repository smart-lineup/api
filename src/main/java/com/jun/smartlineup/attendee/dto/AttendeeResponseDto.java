package com.jun.smartlineup.attendee.dto;

import com.jun.smartlineup.attendee.domain.Attendee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendeeResponseDto {
    private String name;
    private String phone;
    private String info;

    public static AttendeeResponseDto fromDto(Attendee attendee) {
        return AttendeeResponseDto.builder()
                .name(attendee.getName())
                .phone(attendee.getPhone())
                .info(attendee.getInfo())
                .build();
    }
}
