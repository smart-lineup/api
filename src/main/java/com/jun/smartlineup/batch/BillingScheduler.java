package com.jun.smartlineup.batch;

import com.jun.smartlineup.batch.runner.BillingRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingScheduler {

    private final BillingRunner billingRunner;

    @Scheduled(cron = "0 0 2 * * ?")
    public void runScheduledBillingRenewal() {
        // 로그 프레임워크 사용 (예: SLF4J) 권장
        log.info("[Scheduler] Billing renewal 실행");
        try {
            billingRunner.run();
        } catch (Exception e) {
            log.error("[Scheduler] Billing renewal 실행 중 에러 발생", e);
        } finally {
            log.info("[Scheduler] Billing renewal 실행 완료");
        }
    }
}
