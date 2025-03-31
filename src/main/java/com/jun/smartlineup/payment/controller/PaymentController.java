package com.jun.smartlineup.payment.controller;

import com.jun.smartlineup.payment.dto.BillingKeyRequestDto;
import com.jun.smartlineup.payment.dto.PaymentExistDto;
import com.jun.smartlineup.payment.dto.PaymentInfoDto;
import com.jun.smartlineup.payment.service.PaymentService;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/before/info")
    public ResponseEntity<PaymentExistDto> beforeInfo() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        return ResponseEntity.ok(paymentService.beforePayInfo(userDetails));
    }

    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        CustomUserDetails userDetails = UserUtil.getUserDetails();
        paymentService.pay(userDetails);
        return ResponseEntity.ok("ok");
    }
}
