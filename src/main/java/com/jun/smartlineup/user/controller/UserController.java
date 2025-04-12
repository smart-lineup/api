package com.jun.smartlineup.user.controller;

import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.config.email.EmailService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.dto.FeedbackDto;
import com.jun.smartlineup.user.dto.UpdateProfileRequestDto;
import com.jun.smartlineup.user.dto.UserUuidResponseDto;
import com.jun.smartlineup.user.service.UserService;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie responseCookie = jwtTokenProvider.cookieFactory("", 0);
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // Clear the security context
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("logout success");
    }

    @GetMapping("/uuid")
    public ResponseEntity<UserUuidResponseDto> uuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(userService.uuid(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateProfileRequestDto dto,
                                                HttpServletResponse response) {
        ResponseCookie jwtCookie = userService.updateProfile(UserUtil.getUserDetails(), dto);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> feedback(@Valid @RequestBody FeedbackDto dto) throws MessagingException {
        emailService.feedbackEmail(dto.getTitle(), dto.getContent(), dto.getIsPremium());
        return ResponseEntity.ok("ok");
    }
}
