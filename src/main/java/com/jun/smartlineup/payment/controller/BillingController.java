package com.jun.smartlineup.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/billing")
public class BillingController {

    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        return ResponseEntity.ok("ok");
    }
}
