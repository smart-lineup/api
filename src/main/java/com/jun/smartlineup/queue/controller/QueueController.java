package com.jun.smartlineup.queue.controller;

import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.*;
import com.jun.smartlineup.queue.service.QueueService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController {
    private final QueueService queueService;

    @GetMapping("/list")
    public ResponseEntity<List<QueueResponseDto>> list(@RequestParam(name = "line_id") Long lineId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        List<QueueResponseDto> queues = queueService.findQueues(user, lineId);
        return ResponseEntity.ok(queues);
    }

    @PutMapping("/reorder")
    public ResponseEntity<String> reorder(@Valid @RequestBody QueueReorderRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        queueService.reorder(user, dto);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable("id") Long queueId, @RequestBody Map<String, String> map) {
        if (!map.containsKey("status")) {
            return ResponseEntity.badRequest().body("Need status in request");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        queueService.changeStatus(user, queueId, map.get("status"));
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long queueId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        queueService.delete(user, queueId);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/{id}/attendee")
    public ResponseEntity<String> attendeeChange(@PathVariable("id") Long queueId,
                                                 @Valid @RequestBody QueueAttendeeChangeRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        
        queueService.attendeeInfoChange(user, queueId, dto);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/batch-add")
    public ResponseEntity<String> batchAdd(@Valid @RequestBody QueueBatchAddRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        queueService.batchAdd(user, dto);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/add")
    public ResponseEntity<String> addByUser(@Valid @RequestBody QueueAddRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        queueService.addQueueByUser(user, dto);
        return ResponseEntity.ok("ok");
    }

}
