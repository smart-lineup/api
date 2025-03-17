package com.jun.smartlineup.line.service;

import com.jun.smartlineup.line.dto.LineChangeNameRequestDto;
import com.jun.smartlineup.line.dto.LineRemoveRequestDto;
import com.jun.smartlineup.line.dto.LineResponseDto;
import com.jun.smartlineup.user.dto.CustomUserDetails;

import java.util.List;

public interface LineService {
    void add(CustomUserDetails userDetails, String name);
    List<LineResponseDto> getLineList(CustomUserDetails userDetails);
    void remove(CustomUserDetails userDetails, LineRemoveRequestDto dto);

    void changeName(CustomUserDetails userDetails, LineChangeNameRequestDto dto);
}
