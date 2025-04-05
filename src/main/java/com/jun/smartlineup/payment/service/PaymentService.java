package com.jun.smartlineup.payment.service;

import com.jun.smartlineup.exception.ImpossibleRequestException;
import com.jun.smartlineup.exception.NotAvailableRefundException;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.domain.PaymentTransaction;
import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PaymentTransactionRepository;
import com.jun.smartlineup.payment.util.PaymentUtil;
import com.jun.smartlineup.payment.util.TossFailUtil;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import com.jun.smartlineup.utils.WebUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void issueKey(CustomUserDetails userDetails, BillingKeyRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        String tossUrl = "https://api.tosspayments.com/v1/billing/authorizations/issue";
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        ApiResult<BillingIssueKeyResponseDto> apiResult = WebUtil.postTossWithJson(tossUrl,
                secretKey,
                dto,
                BillingIssueKeyResponseDto.class);

        if (apiResult.isFail()) {

        }
        BillingIssueKeyResponseDto responseDto = apiResult.getData();

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElse(Billing.builder()
                .user(user)
                .build());

        billing.issueKey(responseDto);

        billingRepository.save(billing);
    }

    @Transactional
    public void payInfo(CustomUserDetails userDetails, PaymentInfoAddDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElse(Billing.builder().user(user).build());

        billing.changeInfo(dto);
        billingRepository.save(billing);
    }

    public PaymentInfoResponseDto existPayInfo(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new RuntimeException("Impossible request::exist::user=" + user.getEmail()));

        if (billing.getEndedAt() != null) {
            return PaymentInfoResponseDto.exist(billing);
        }
        return PaymentInfoResponseDto.notExist();
    }

    @Transactional
    public PayResponseDto pay(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new RuntimeException("Impossible request::pay::user=" + user.getEmail()));
        if (billing.getEndedAt() != null && billing.getEndedAt().isAfter(LocalDate.now())) {
            throw new RuntimeException("Impossible to call api::pay::user=" + user.getEmail());
        }

        String url = "https://api.tosspayments.com/v1/billing/" + billing.getBillingKey();
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        PaymentPayRequestDto dto = PaymentPayRequestDto.builder()
                .customerKey(billing.getCustomerKey())
                .amount(billing.getPrice())
                .orderId("order-" + user.getId() + "-" + System.currentTimeMillis())
                .orderName("Smart Line up " + billing.getPlanType().getKorean() + " 구독")
                .customerEmail(user.getEmail())
                .customerName(user.getName())
                .taxFreeAmount(BigDecimal.valueOf(0))
                .build();

        ApiResult<TossPaymentResponseDto> apiResult = WebUtil.postTossWithJson(url,
                secretKey,
                dto,
                TossPaymentResponseDto.class);

        if (apiResult.isFail()) {
            TossFailDto error = apiResult.getError();
            System.out.println(apiResult);
            TossFailDto.ErrorDetail detail = error.getError();
            // add logging
            if (TossFailUtil.isFailBaseOnUser(detail.getCode())) {
                return PayResponseDto.builder()
                        .isSuccess(false)
                        .code("400")
                        .message(TossFailUtil.getMessageBasedOnCode(detail.getCode()))
                        .build();
            }

            return PayResponseDto.builder()
                    .isSuccess(false)
                    .code("500")
                    .message("예기치 못한 에러가 발생하였습니다. 문의 부탁드립니다.")
                    .build();
        }
        TossPaymentResponseDto responseDto = apiResult.getData();

        PaymentTransaction paymentTransaction = PaymentTransaction.successWithToss(user, billing, responseDto);
        paymentTransactionRepository.save(paymentTransaction);

        billing.subscribe();
        return PayResponseDto.builder()
                .isSuccess(true)
                .code("200")
                .message("ok")
                .build();
    }

    @Transactional
    public void changePlanType(CustomUserDetails userDetails, BillingPlanTypeRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new RuntimeException("Impossible request::changePlanType::user=" + user.getEmail()));

        if (billing.getStatus() != BillingStatus.ACTIVE) {
            throw new RuntimeException("Impossible request::changePlanType::user=" + user.getEmail());
        }

        billing.changePlanType(dto.getPlanType());
    }

    public List<PaymentHistoryDto> history(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        return paymentTransactionRepository.findByUser(user).stream()
                .map(PaymentHistoryDto::fromDto)
                .toList();
    }

    @Transactional
    public void refund(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new ImpossibleRequestException("refund", user));

        if (!PaymentUtil.isRefundable(billing.getStartedAt())) {
            throw new NotAvailableRefundException();
        }

        paymentTransactionRepository.findByUser(user);
    }
}
