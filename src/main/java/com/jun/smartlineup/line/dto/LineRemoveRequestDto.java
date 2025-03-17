package com.jun.smartlineup.line.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LineRemoveRequestDto {
    @NotBlank
    private Long id;
}
