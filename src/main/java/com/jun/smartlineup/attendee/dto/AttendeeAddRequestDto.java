package com.jun.smartlineup.attendee.dto;

import jakarta.validation.constraints.NotBlank;
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
    private String phone;
}
