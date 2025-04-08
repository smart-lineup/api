package com.jun.smartlineup.batch.runner;

import com.jun.smartlineup.batch.processor.BillingProcessor;
import com.jun.smartlineup.common.exception.BatchBillingFailException;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.repository.BillingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingRunner {

    private final BillingRepository billingRepository;
    private final BillingProcessor billingProcessor;

    public void run() {
        log.info("Billing Runner 시작 시간: {}", LocalDateTime.now());
        List<Billing> billingList = billingRepository.findAllActiveBeforeToday(LocalDate.now(), BillingStatus.ACTIVE);
        log.info("Billing Runner 시작 - 처리 대상: {}건", billingList.size());

        if (billingList.isEmpty()) {
            log.info("처리할 Billing 없음 - 종료합니다.");
            return;
        }

        int success = 0;
        int fail = 0;
        StringBuilder failedBillingIds = new StringBuilder();
        for (Billing billing : billingList) {
            try {
                billingProcessor.process(billing);
                success++;
            } catch (Exception e) {
                log.warn("결제 실패: billingId={}, userId={}", billing.getId(), billing.getUser().getId(), e);
                fail++;
                if (!failedBillingIds.isEmpty()) {
                    failedBillingIds.append(", "); // 공백 대신 쉼표로 구분
                }
                failedBillingIds.append(billing.getId());
            }
        }
        log.info("Billing Runner 종료 - 성공: {}건, 실패: {}건", success, fail);
        log.info("Billing Runner 종료 시간: {}", LocalDateTime.now());

        if (fail > 0) {
            throw new BatchBillingFailException(failedBillingIds.toString());
        }
    }
}
