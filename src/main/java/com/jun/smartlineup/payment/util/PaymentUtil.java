package com.jun.smartlineup.payment.util;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.utils.WebUtil;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

public class PaymentUtil {
    public static boolean isRefundable(Billing billing) {
        if (billing.getStatus() != BillingStatus.ACTIVE) {
            return false;
        }
        LocalDate refundDeadline = billing.getStartedAt().plusDays(1);
        return !LocalDate.now().isAfter(refundDeadline);
    }

    public static ApiResult<BillingIssueKeyResponseDto> issueKeyToToss(BillingKeyRequestDto dto, String tossSecretKey) {
        String tossUrl = "https://api.tosspayments.com/v1/billing/authorizations/issue";
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return WebUtil.postTossWithJson(tossUrl,
                secretKey,
                dto,
                BillingIssueKeyResponseDto.class);
    }

    public static ApiResult<TossPaymentResponseDto> payToToss(Billing billing, User user, String tossSecretKey) {
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

        return WebUtil.postTossWithJson(url,
                secretKey,
                dto,
                TossPaymentResponseDto.class);
    }


    public static ApiResult<TossPaymentResponseDto> refundToToss(String paymentKey, String tossSecretKey) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
        String secretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        RefundApiRequestDto apiRequestDto = RefundApiRequestDto.builder()
                .cancelReason("단순 변심")
                .build();

        return WebUtil.postTossWithJson(
                url,
                secretKey,
                apiRequestDto,
                TossPaymentResponseDto.class);
    }

}
