package com.jun.smartlineup.batch.runner;


import com.jun.smartlineup.batch.processor.BillingProcessor;
import com.jun.smartlineup.common.exception.BatchBillingFailException;
import com.jun.smartlineup.payment.domain.*;
import com.jun.smartlineup.payment.dto.ApiResult;
import com.jun.smartlineup.payment.dto.PaymentPayRequestDto;
import com.jun.smartlineup.payment.dto.TossErrorResponse;
import com.jun.smartlineup.payment.dto.TossPaymentResponseDto;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PaymentTransactionRepository;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.utils.WebUtil;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BillingRunnerTest {

    @Autowired
    private BillingRepository billingRepository;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private UserRepository userRepository;

    private BillingProcessor billingProcessor;
    private BillingRunner billingRunner;

    private MockedStatic<WebUtil> webUtilMock;

    @BeforeEach
    void setUp() {
        billingProcessor = new BillingProcessor(billingRepository, paymentTransactionRepository);
        billingRunner = new BillingRunner(billingRepository, billingProcessor);

        billingRepository.deleteAll();
        paymentTransactionRepository.deleteAll();

        webUtilMock = mockStatic(WebUtil.class);
    }

    @AfterEach
    void tearDown() {
        webUtilMock.close();
    }

    @Test
    @DisplayName("정상적으로 빌링 리스트를 처리할 수 있다")
    void run_shouldCreateTransaction_whenBillingNeedsRenewal() {
        // given
        for (int i = 0; i < 50; i++) {
            User user = createNewUser(i);
            userRepository.save(user);

            Billing billing = createBilling(user, PlanType.MONTHLY);
            billingRepository.save(billing);
        }
        for (int i = 50; i < 100; i++) {
            User user = createNewUser(i);
            userRepository.save(user);

            Billing billing = createBilling(user, PlanType.ANNUAL);
            billingRepository.save(billing);
        }

        TossPaymentResponseDto tossResponse = getTossPaymentResponseDto(1);
        ApiResult<TossPaymentResponseDto> dummyApiResult = ApiResult.success(tossResponse);
        webUtilMock.when(() -> WebUtil.postTossWithJson(
                contains("https://api.tosspayments.com/v1/billing/"),
                anyString(),
                any(PaymentPayRequestDto.class),
                eq(TossPaymentResponseDto.class)
        )).thenReturn(dummyApiResult);

        // when
        billingRunner.run();

        // then
        Assertions.assertEquals(100, paymentTransactionRepository.count());
    }

    @Test
    @DisplayName("Billing 조건이 충족되지 않으면 ImpossibleRequestException 발생")
    void run_shouldThrowImpossibleRequestException_whenBillingConditionsNotMet() {
        // given
        User user = createNewUser(1);
        userRepository.save(user);

        Billing billing = createBilling(user, PlanType.MONTHLY);
        billing.changeRenewal(false);
        billingRepository.save(billing);

        // when, then
        Exception ex = Assertions.assertThrows(BatchBillingFailException.class, () -> {
            billingRunner.run();
        });
        Assertions.assertTrue(ex.getMessage().contains("Billing processing failed"));
    }

    @Test
    @DisplayName("API 실패 시 BatchBillingApiException 발생, Billing & User 상태 업데이트")
    void run_shouldThrowBatchBillingApiException_whenApiCallFails() {
        // given
        User user = createNewUser(2);
        userRepository.save(user);

        Billing billing = createBilling(user, PlanType.ANNUAL);
        billingRepository.save(billing);

        mockFailFromToss();

        // when
        Exception ex = Assertions.assertThrows(BatchBillingFailException.class, () -> {
            billingRunner.run();
        });
        Assertions.assertTrue(ex.getMessage().contains("Billing processing failed"));

        // then
        Billing updatedBilling = billingRepository.findById(billing.getId()).orElse(null);
        Assertions.assertNotNull(updatedBilling);
        Assertions.assertEquals(BillingStatus.EXPIRED, updatedBilling.getStatus());

        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(Role.FREE, updatedUser.getRole());

        Optional<PaymentTransaction> optionalPaymentTransaction = paymentTransactionRepository.findFirstByUserOrderByCreatedAtDesc(user);
        PaymentTransaction transaction = optionalPaymentTransaction.orElse(null);
        Assertions.assertNotNull(transaction);
        Assertions.assertEquals(PayStatus.FAIL, transaction.getStatus());
        Assertions.assertNotNull(transaction.getFailCode());
        Assertions.assertNotNull(transaction.getFailMessage());
    }

    @Test
    @DisplayName("Billing 이 없으면 processor 가 호출되지 않는다")
    void run_shouldNotCallProcessor_whenNoBillingExists() {
        // given
        BillingRepository mockBillingRepo = mock(BillingRepository.class);
        BillingProcessor mockProcessor = mock(BillingProcessor.class);
        BillingRunner runner = new BillingRunner(mockBillingRepo, mockProcessor);

        when(mockBillingRepo.findAllActiveBeforeToday(any(), eq(BillingStatus.ACTIVE)))
                .thenReturn(List.of());

        // when
        runner.run();

        // then
        verify(mockProcessor, never()).process(any());
    }

    @Test
    @DisplayName("중복 결제 방지를 위해 이미 오늘 결제된 Billing 은 처리되지 않아야 한다")
    void run_shouldNotProcessBilling_whenDuplicateTransactionExists() {
        // given
        User user = createNewUser(999);
        userRepository.save(user);
        Billing billing = createBilling(user, PlanType.MONTHLY);
        billingRepository.save(billing);

        PaymentTransaction existingTx = PaymentTransaction.builder()
                .user(user)
                .billing(billing)
                .amount(billing.getPrice())
                .paymentMethod(PaymentMethod.TOSS)
                .orderId("order-dup")
                .paymentKey("dummy-key")
                .mid("dummy-mid")
                .receiptUrl("dummy-receipt")
                .status(PayStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();
        paymentTransactionRepository.save(existingTx);

        long beforeCount = paymentTransactionRepository.count();

        // when
        Exception ex = Assertions.assertThrows(BatchBillingFailException.class, () -> {
            billingRunner.run();
        });

        // then
        Assertions.assertTrue(ex.getMessage().contains("Billing processing failed"));
        long afterCount = paymentTransactionRepository.count();
        Assertions.assertEquals(beforeCount, afterCount);
    }

    private Billing createBilling(User user, PlanType planType) {
        Billing billing = Billing.builder()
                .user(user)
                .planType(planType)
                .price(planType.getPrice())
                .paymentProvider(PaymentMethod.TOSS)
                .startedAt(LocalDate.now().minusMonths(1))
                .endedAt(LocalDate.now().minusDays(1))
                .status(BillingStatus.ACTIVE)
                .renewal(true)
                .build();
        return billing;
    }

    private User createNewUser(int num) {
        User user = User.builder()
                .email("test" + num + "@example.com")
                .name("Tester" + num)
                .password("pw")
                .uuid(UUID.randomUUID().toString())
                .role(Role.PREMIUM)
                .privacyAgreed(true)
                .privacyAgreedAt(LocalDateTime.now())
                .isVerified(true)
                .isOAuthLogin(false)
                .build();
        return user;
    }

    private TossPaymentResponseDto getTossPaymentResponseDto(int num) {
        TossPaymentResponseDto tossResponse = new TossPaymentResponseDto();
        tossResponse.setOrderId("order-" + num);
        tossResponse.setPaymentKey("dummy-payment-key-" + num);
        tossResponse.setMId("dummy-mid-" + num);
        tossResponse.setReceiptUrl("http://dummy.receipt.url");
        tossResponse.setStatus("PAID");
        return tossResponse;
    }

    private void mockFailFromToss() {
        TossErrorResponse.ErrorDetail errorDetail = TossErrorResponse.ErrorDetail.builder()
                .code("JUST_ERROR_CODE")
                .message("JUST_ERROR_MESSAGE")
                .build();
        TossErrorResponse tossErrorResponse = TossErrorResponse.builder()
                .error(errorDetail)
                .build();
        ApiResult<TossPaymentResponseDto> dummyApiResult = ApiResult.failure(tossErrorResponse);

        webUtilMock.when(() -> WebUtil.postTossWithJson(
                contains("https://api.tosspayments.com/v1/billing/"),
                anyString(),
                any(PaymentPayRequestDto.class),
                eq(TossPaymentResponseDto.class)
        )).thenReturn(dummyApiResult);
    }
}