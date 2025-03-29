package com.jun.smartlineup.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCheckResponseDto {
    private String name;
    private String email;
}
