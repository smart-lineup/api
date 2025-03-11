package com.jun.smartlineup.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyTokenDto {
    @NotBlank
    private String email;
    @Size(min = 6, max = 6)
    private String token;
}
