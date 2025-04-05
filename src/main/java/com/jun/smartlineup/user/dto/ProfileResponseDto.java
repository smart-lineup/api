package com.jun.smartlineup.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDto {
    private String name;
    private String email;
}
