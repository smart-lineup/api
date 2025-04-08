package com.jun.smartlineup.payment.service;

import com.jun.smartlineup.common.exception.ImpossibleRequestException;
import com.jun.smartlineup.common.exception.NotAvailableRefundException;
import com.jun.smartlineup.common.exception.TossApiException;
import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.domain.PaymentCancel;
import com.jun.smartlineup.payment.domain.PaymentTransaction;
import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PaymentCancelRepository;
import com.jun.smartlineup.payment.repository.PaymentTransactionRepository;
import com.jun.smartlineup.payment.util.PaymentUtil;
import com.jun.smartlineup.payment.util.TossFailUtil;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void issueKey(CustomUserDetails userDetails, BillingKeyRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        ApiResult<BillingIssueKeyResponseDto> apiResult = PaymentUtil.issueKeyToToss(dto, tossSecretKey);

        if (apiResult.isFail()) {
            throw new TossApiException(apiResult.getError());
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
            throw new ImpossibleRequestException("pay", user);
        }

        ApiResult<TossPaymentResponseDto> apiResult = PaymentUtil.payToToss(billing, user, tossSecretKey);

        if (apiResult.isFail()) {
            PaymentTransaction transaction = PaymentTransaction.payFailWithToss(billing, apiResult.getError());
            paymentTransactionRepository.save(transaction);
            return TossFailUtil.getPayResponseDtoForFail(apiResult.getError());
        }
        TossPaymentResponseDto responseDto = apiResult.getData();

        PaymentTransaction paymentTransaction = PaymentTransaction.payWithToss(billing, responseDto);
        paymentTransactionRepository.save(paymentTransaction);

        billing.subscribe();
        user.updateRole(Role.PREMIUM);

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

        return paymentTransactionRepository.findByUserWithCancel(user).stream()
                .map(PaymentHistoryDto::fromDto)
                .toList();
    }

    @Transactional
    public void refund(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new ImpossibleRequestException("refund", user));

        if (!PaymentUtil.isRefundable(billing)) {
            throw new NotAvailableRefundException();
        }

        Optional<PaymentTransaction> optionalPaymentTransaction = paymentTransactionRepository.findFirstByUserOrderByCreatedAtDesc(user);
        PaymentTransaction transaction = optionalPaymentTransaction.orElseThrow(() -> new ImpossibleRequestException("refund", user));
        String paymentKey = transaction.getPaymentKey();

        ApiResult<TossPaymentResponseDto> apiResult = PaymentUtil.refundToToss(paymentKey, tossSecretKey);

        if (apiResult.isFail()) {
            TossErrorResponse error = apiResult.getError();
            throw new TossApiException(error);
        }

        TossPaymentResponseDto responseDto = apiResult.getData();
        List<PaymentCancel> paymentCancels = PaymentCancel.cancelWithToss(user, transaction, responseDto, "user");
        paymentCancelRepository.saveAll(paymentCancels);

        billing.refund();
        user.updateRole(Role.FREE);
    }

    @Transactional
    public void cancel(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        Billing billing = optionalBilling.orElseThrow(() -> new ImpossibleRequestException("refund", user));

        billing.cancel();
    }
}
