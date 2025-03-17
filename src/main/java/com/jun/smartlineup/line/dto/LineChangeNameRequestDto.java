package com.jun.smartlineup.line.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LineChangeNameRequestDto {
    @NotBlank
    private Long id;
    @NotBlank
    private String name;
}
