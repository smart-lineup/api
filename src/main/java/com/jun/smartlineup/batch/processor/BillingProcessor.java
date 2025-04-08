package com.jun.smartlineup.batch.processor;

import com.jun.smartlineup.common.exception.BatchBillingApiException;
import com.jun.smartlineup.common.exception.BatchTryDoublePayException;
import com.jun.smartlineup.common.exception.ImpossibleRequestException;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.domain.PaymentTransaction;
import com.jun.smartlineup.payment.dto.ApiResult;
import com.jun.smartlineup.payment.dto.TossPaymentResponseDto;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PaymentTransactionRepository;
import com.jun.smartlineup.payment.util.PaymentUtil;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingProcessor {
    private final BillingRepository billingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void process(Billing billing) {
        User user = billing.getUser();
        log.info("Billing Process Start - billingId: {}, userId: {}", billing.getId(), user.getId());
        if (!billing.getRenewal() ||
                billing.getStatus() != BillingStatus.ACTIVE ||
                user.getRole() != Role.PREMIUM
        ) {
            throw new ImpossibleRequestException("batch billing process");
        }
        if (paymentTransactionRepository.existsByBillingAndCreatedAtBetween(
                billing,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay())
        ) {
            throw new BatchTryDoublePayException(billing);
        }

        ApiResult<TossPaymentResponseDto> apiResult = PaymentUtil.payToToss(billing, user, tossSecretKey);
        if (apiResult.isFail()) {
            billing.batchFail();
            user.updateRole(Role.FREE);

            PaymentTransaction transaction = PaymentTransaction.payFailWithToss(billing, apiResult.getError());
            paymentTransactionRepository.save(transaction);
            throw new BatchBillingApiException(apiResult.getError(), billing);
        }

        TossPaymentResponseDto responseDto = apiResult.getData();
        PaymentTransaction paymentTransaction = PaymentTransaction.payWithToss(billing, responseDto);
        paymentTransactionRepository.save(paymentTransaction);

        billing.subscribe();
        user.updateRole(Role.PREMIUM);
        billingRepository.save(billing);
        log.info("Billing Process Success - billingId: {}, paymentKey: {}", billing.getId(), responseDto.getPaymentKey());
    }
}
