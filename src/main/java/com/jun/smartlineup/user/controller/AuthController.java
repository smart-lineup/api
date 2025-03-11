package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.user.dto.*;
import com.jun.smartlineup.user.service.PasswordResetService;
import com.jun.smartlineup.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

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

    @PostMapping("/find/id")
    public ResponseEntity<String> findId(@Valid @RequestBody FindIdDto findIdDto) {
        userService.findByEmail(findIdDto.getEmail());
        return ResponseEntity.ok("exist");
    }

    @PostMapping("/password/request")
    public ResponseEntity<String> requestReset(@RequestBody Map<String, String> body) throws MessagingException {
        String email = body.get("email");
        passwordResetService.sendResetEmail(email);
        return ResponseEntity.ok("Send a verify token to user email.");
    }

    @PostMapping("/password/verify")
    public ResponseEntity<String> verifyToken(@Valid @RequestBody VerifyTokenDto verifyTokenDto) {
        if (passwordResetService.verifyToken(verifyTokenDto.getEmail(), verifyTokenDto.getToken())) {
            return ResponseEntity.ok("Success verify");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fail verify");
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        passwordResetService.changePassword(dto);
        return ResponseEntity.ok("password is changed");
    }

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return ResponseEntity.ok("login yet");
        }

        User user = (User) authentication.getPrincipal();
        System.out.println(user);
        com.jun.smartlineup.user.domain.User actaulUser = userService.findByEmail(user.getUsername());

        return ResponseEntity.ok(actaulUser.getName());
    }
}
