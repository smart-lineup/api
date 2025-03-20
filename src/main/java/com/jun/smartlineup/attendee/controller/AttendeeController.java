package com.jun.smartlineup.attendee.controller;

import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.attendee.service.AttendeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendee")
public class AttendeeController {
    private final AttendeeService attendeeService;

    @PostMapping("/add")
    public ResponseEntity<String> add(@Valid @RequestBody AttendeeAddRequestDto dto) {
        attendeeService.add(dto);
        return ResponseEntity.ok("ok");
    }
}
