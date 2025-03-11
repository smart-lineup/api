package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new RuntimeException("User not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        System.out.println(user);
        com.jun.smartlineup.user.domain.User actaulUser = userService.findByEmail(user.getUsername());

        return ResponseEntity.ok(actaulUser.getName());
    }
}
