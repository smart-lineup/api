package com.jun.smartlineup.attendee.dto;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendeeAddRequestDto {
    private String uuid;
    @NotBlank
    private String name;
    @Size(min = 3)
    @Pattern(regexp = "^\\d{2,3}-\\d{4}-\\d{4}$", message = "전화번호는 02-0000-0000 또는 010-0000-0000 형식이어야 합니다.")
    private String phone;
    private String info;

    public Attendee toEntity(User user) {
        if (info.isEmpty()){
            info = "{}";
        }
        return Attendee.builder()
                .user(user)
                .name(name)
                .phone(phone)
                .info(info)
                .build();
    }
}
