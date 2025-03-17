package com.jun.smartlineup.line.controller;

import com.jun.smartlineup.line.dto.LineChangeNameRequestDto;
import com.jun.smartlineup.line.dto.LineRemoveRequestDto;
import com.jun.smartlineup.line.dto.LineResponseDto;
import com.jun.smartlineup.line.service.LineService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/line")
public class LineController {

    private final LineService lineService;

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Map<String, String> map) {
        String name = map.getOrDefault("name", "");
        if (name.isEmpty()) {
            ResponseEntity.badRequest().body("No exist name in request");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        lineService.add(user, name);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/list")
    public ResponseEntity<List<LineResponseDto>> getList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        List<LineResponseDto> lineList = lineService.getLineList(user);

        return ResponseEntity.ok(lineList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        lineService.remove(user, id);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/name")
    public ResponseEntity<String> changeName(@Valid @RequestBody LineChangeNameRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        lineService.changeName(user, dto);
        return ResponseEntity.ok("ok");
    }
}
