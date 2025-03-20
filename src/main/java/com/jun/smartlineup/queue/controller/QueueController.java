package com.jun.smartlineup.queue.controller;

import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.QueueResponseDto;
import com.jun.smartlineup.queue.service.QueueService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController {
    private final QueueService queueService;

    @GetMapping("/list")
    public ResponseEntity<List<QueueResponseDto>> list(@RequestParam(name = "line_id") Long lineId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        List<Queue> queues = queueService.findQueues(user, lineId);
        List<QueueResponseDto> list = queues.stream().map(QueueResponseDto::fromEntity).toList();
        return ResponseEntity.ok(list);
    }

//    @PostMapping("/add")
//    public ResponseEntity<String> add()
}
