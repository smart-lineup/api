package com.jun.smartlineup.line.dto;

import com.jun.smartlineup.line.domain.Line;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LineResponseDto {
    private Long id;
    private String name;
    private String uuid;

    public static LineResponseDto fromEntity(Line line) {
        return LineResponseDto.builder()
                .id(line.getId())
                .name(line.getName())
                .uuid(line.getUuid())
                .build();
    }
}
