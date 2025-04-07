package com.jun.smartlineup.batch;

import com.jun.smartlineup.batch.runner.BillingRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BillingScheduler {

    private final BillingRunner billingRunner;

    public BillingScheduler(BillingRunner billingRunner) {
        this.billingRunner = billingRunner;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void runScheduledBillingRenewal() {
        System.out.println("[Scheduler] Billing renewal 실행");
        billingRunner.run();
    }
}
