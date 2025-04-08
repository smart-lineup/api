package com.jun.smartlineup.payment.controller;

import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.service.PaymentService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.service.UserService;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/issue/key")
    public ResponseEntity<String> issueKey(@Valid @RequestBody BillingKeyRequestDto dto) {
        CustomUserDetails userDetails = UserUtil.getUserDetails();

        paymentService.issueKey(userDetails, dto);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/info")
    public ResponseEntity<String> info(@Valid @RequestBody PaymentInfoAddDto dto) {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        paymentService.payInfo(userDetails, dto);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/info")
    public ResponseEntity<PaymentInfoResponseDto> payInfo() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        return ResponseEntity.ok(paymentService.existPayInfo(userDetails));
    }

    @PostMapping("/pay")
    public ResponseEntity<PayResponseDto> pay() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        PayResponseDto payDto = paymentService.pay(userDetails);
        ResponseCookie responseCookie = userService.tokenReset(userDetails);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(payDto);
    }

    @GetMapping("/plan-type")
    public ResponseEntity<PlanTypeDto> planTypeInfo() {
        return ResponseEntity.ok(PlanTypeDto.create());
    }

    @PutMapping("/plan-type")
    public ResponseEntity<String> changePlanType(@Valid @RequestBody BillingPlanTypeRequestDto dto) {
        paymentService.changePlanType(UserUtil.getUserDetails(), dto);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentHistoryDto>> history() {
        return ResponseEntity.ok(paymentService.history(UserUtil.getUserDetails()));
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refund() {
        paymentService.refund(UserUtil.getUserDetails());
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/cancel")
    public ResponseEntity<String> cancel() {
        paymentService.cancel(UserUtil.getUserDetails());
        return ResponseEntity.ok("ok");
    }
}
