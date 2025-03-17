package com.jun.smartlineup.line.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LineChangeNameRequestDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
}
