package com.jun.smartlineup.payment.controller;

import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.service.PaymentService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/issue/key")
    public ResponseEntity<String> issueKey(@Valid @RequestBody BillingKeyRequestDto dto) {
        CustomUserDetails userDetails = UserUtil.getUserDetails();

        paymentService.issueKey(userDetails, dto);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/info")
    public ResponseEntity<String> info(@Valid @RequestBody PaymentInfoDto dto) {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        paymentService.payInfo(userDetails, dto);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/info")
    public ResponseEntity<PaymentExistDto> payInfo() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        return ResponseEntity.ok(paymentService.existPayInfo(userDetails));
    }

    @PostMapping("/pay")
    public ResponseEntity<PayResponseDto> pay() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        PayResponseDto payDto = paymentService.pay(userDetails);
        return ResponseEntity.ok(payDto);
    }

    @PutMapping("/plan-type")
    public ResponseEntity<String> changePlanType(@Valid @RequestBody BillingPlanTypeRequestDto dto) {
        paymentService.changePlanType(UserUtil.getUserDetails(), dto);

        return ResponseEntity.ok("ok");
    }
}
