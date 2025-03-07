package com.jun.smartlineup.line.controller;

import com.jun.smartlineup.line.dto.LineResponseDto;
import com.jun.smartlineup.line.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/line")
public class LineController {

    private final LineService lineService;

    @GetMapping("/list")
    public ResponseEntity<LineResponseDto> getList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        System.out.println(authentication.getPrincipal());
        System.out.println((User) authentication.getPrincipal());

        return ResponseEntity.ok(new LineResponseDto());
    }
}
