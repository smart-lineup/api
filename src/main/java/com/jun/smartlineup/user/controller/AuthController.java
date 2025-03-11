package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.user.dto.LoginRequestDto;
import com.jun.smartlineup.user.dto.SignupRequestDto;
import com.jun.smartlineup.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) throws MessagingException {
        userService.signup(signupRequestDto);
        return ResponseEntity.ok("please check the email");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, Object> data) {
        String token = (String) data.get("token");
        userService.verifyEmail(token);
        return ResponseEntity.ok("success email verification");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                        HttpServletResponse response) {
        Cookie tokenCookie = userService.login(loginRequestDto);
        response.addCookie(tokenCookie);
        return ResponseEntity.ok("login success");
    }
}
