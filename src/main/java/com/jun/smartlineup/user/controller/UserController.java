package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;



    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie responseCookie = jwtTokenProvider.cookieFactory("", 0);
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // Clear the security context
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("logout success");
    }
}
