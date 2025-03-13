package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.user.dto.*;
import com.jun.smartlineup.user.service.PasswordResetService;
import com.jun.smartlineup.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    private final JwtTokenProvider jwtTokenProvider;

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
        ResponseCookie jwtCookie = userService.login(loginRequestDto);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
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
}
