package com.jun.smartlineup.payment.service;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.Purchase;
import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PurchaseRepository;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import com.jun.smartlineup.utils.WebUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void issueKey(CustomUserDetails userDetails, BillingKeyRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        String tossUrl = "https://api.tosspayments.com/v1/billing/authorizations/issue";
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        BillingIssueKeyResponseDto responseDto = WebUtil.postWithJson(tossUrl,
                secretKey,
                dto,
                new ParameterizedTypeReference<>() {});

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElse(Billing.builder()
                .user(user)
                .build());

        billing.issueKey(responseDto);
        System.out.println(responseDto.getCardNumber());

        billingRepository.save(billing);
    }

    @Transactional
    public void payInfo(CustomUserDetails userDetails, PaymentInfoDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElse(Billing.builder().user(user).build());

        billing.changeInfo(dto);
        billingRepository.save(billing);
    }

    public PaymentExistDto exist(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new RuntimeException("Impossible situation::exist::user=" + user.getEmail()));

        PaymentExistDto paymentExistDto = new PaymentExistDto();
        paymentExistDto.setIsExist(billing.getBillingKey() != null);
        return paymentExistDto;
    }

    @Transactional
    public void pay(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new RuntimeException("Impossible situation::pay::user=" + user.getEmail()));

        String url = "https://api.tosspayments.com/v1/billing/" + billing.getBillingKey();
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        PaymentPayRequestDto dto = PaymentPayRequestDto.builder()
                .customerKey(billing.getCustomerKey())
                .amount(billing.getPrice())
                .orderId("order-" + user.getId() + "-" + System.currentTimeMillis())
                .orderName("Smart Line up " + billing.getPlanType().getKorean() + " 구독")
                .customerEmail(user.getEmail())
                .customerName(user.getName())
                .taxFreeAmount(0L)
                .build();


        TossPaymentResponseDto responseDto = WebUtil.postWithJson(url,
                secretKey,
                dto,
                new ParameterizedTypeReference<>() {});

        Purchase purchase = Purchase.successWithToss(user, billing, responseDto);
        purchaseRepository.save(purchase);

        billing.subscribe();
    }
}
