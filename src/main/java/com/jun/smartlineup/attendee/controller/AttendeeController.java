package com.jun.smartlineup.attendee.controller;

import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.attendee.dto.AttendeeDeleteRequestDto;
import com.jun.smartlineup.attendee.dto.AttendeePositionResponseDto;
import com.jun.smartlineup.attendee.dto.CanJoinResponseDto;
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

    @GetMapping("/{uuid}/can-join")
    public ResponseEntity<CanJoinResponseDto> canJoin(@PathVariable String uuid) {
        boolean canJoin = attendeeService.attendeeCanJoin(uuid);
        return ResponseEntity.ok(new CanJoinResponseDto(canJoin));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AttendeePositionResponseDto> getPosition(@PathVariable String uuid, @RequestParam("phone") String phone) {
        return ResponseEntity.ok(attendeeService.findPosition(uuid, phone));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> delete(@Valid @RequestBody AttendeeDeleteRequestDto dto) {
        attendeeService.deleteAttendee(dto);
        return ResponseEntity.ok("ok");
    }
}
