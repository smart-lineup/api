package com.jun.smartlineup.payment.service;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.PlanType;
import com.jun.smartlineup.payment.dto.*;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.payment.repository.PaymentTransactionRepository;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.utils.WebUtil;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PaymentServiceTest {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    private PaymentService paymentService;
    private MockedStatic<WebUtil> webUtilMock;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(billingRepository, userRepository, paymentTransactionRepository);

        billingRepository.deleteAll();
        paymentTransactionRepository.deleteAll();
        userRepository.deleteAll();

        webUtilMock = mockStatic(WebUtil.class);
    }

    @AfterEach
    void tearDown() {
        webUtilMock.close();
    }

    // 테스트용 User 생성 헬퍼 메서드
    private User createTestUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@test.com")
                .uuid("dummy-uuid")
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    private CustomUserDetails createDummyCustomUserDetails(String email) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        return userDetails;
    }

    @Test
    @DisplayName("Test issueKey: Successful billing key issuance sets billing fields correctly")
    void testIssueKeySuccess() {
        // Arrange
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());
        BillingKeyRequestDto requestDto = BillingKeyRequestDto.builder()
                .authKey("aa")
                .customerKey("bb")
                .build();

        // 더미 응답 DTO 생성
        BillingIssueKeyResponseDto responseDto = new BillingIssueKeyResponseDto();
        responseDto.setBillingKey("dummy-billing-key");
        responseDto.setCustomerKey("dummy-customer-key");
        responseDto.setCardNumber("123456781234567*");

        ApiResult<BillingIssueKeyResponseDto> dummyApiResult = ApiResult.success(responseDto);

        webUtilMock.when(() -> WebUtil.postTossWithJson(
                anyString(), anyString(), eq(requestDto), eq(BillingIssueKeyResponseDto.class)
        )).thenReturn(dummyApiResult);

        // Act
        paymentService.issueKey(userDetails, requestDto);

        // Assert: Billing이 올바르게 업데이트되었는지 확인
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        assertTrue(optionalBilling.isPresent(), "Billing should be present after issuing key");
        Billing billing = optionalBilling.get();
        assertEquals("dummy-billing-key", billing.getBillingKey());
        assertEquals("dummy-customer-key", billing.getCustomerKey());
        assertEquals("567*", billing.getCardLastNumber());
    }

    @Test
    @DisplayName("Test payInfo: Billing info is updated correctly")
    void testPayInfoSuccess() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder().user(user).build();
        billingRepository.save(billing);

        PaymentInfoDto infoDto = new PaymentInfoDto();
        infoDto.setPrice(BigDecimal.valueOf(9_900));
        infoDto.setPlanType(PlanType.MONTHLY);

        // Act
        paymentService.payInfo(userDetails, infoDto);

        // Assert: Billing 정보가 업데이트되었는지 확인
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        assertTrue(optionalBilling.isPresent(), "Billing should be present after updating info");
        Billing updatedBilling = optionalBilling.get();
        assertEquals(BigDecimal.valueOf(9_900), updatedBilling.getPrice());
        assertEquals(PlanType.MONTHLY, updatedBilling.getPlanType());
    }

    @Test
    @DisplayName("Test beforePayInfo: Returns Exist Full data dto")
    void testPayInfoExist() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .cardLastNumber("111*")
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now().plusDays(1))
                .build();
        billingRepository.save(billing);

        // Act
        PaymentExistDto existDto = paymentService.existPayInfo(userDetails);

        // Assert
        assertTrue(existDto.getIsExist());
        assertNotNull(existDto.getCardLastNumber());
        assertNotNull(existDto.getIsSubscribe());
        assertNotNull(existDto.getEndAt());
        assertNotNull(existDto.getPlanType());
        assertNotNull(existDto.getStatus());
    }

    @Test
    @DisplayName("Test beforePayInfo: Returns Not Exist data dto")
    void testPayInfoNotExist() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder().user(user).build();
        billingRepository.save(billing);

        // Act
        PaymentExistDto existDto = paymentService.existPayInfo(userDetails);

        // Assert
        assertFalse(existDto.getIsExist());
        assertNull(existDto.getCardLastNumber());
        assertNull(existDto.getIsSubscribe());
        assertNull(existDto.getEndAt());
        assertNull(existDto.getPlanType());
        assertNull(existDto.getStatus());
    }

    @Test
    @DisplayName("Test pay: Successful payment transaction updates billing and saves transaction")
    void testPaySuccess() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now().plusDays(1))
                .build();
        billingRepository.save(billing);

        TossPaymentResponseDto tossResponse = new TossPaymentResponseDto();
        tossResponse.setOrderId("order-12345");
        tossResponse.setPaymentKey("dummy-payment-key");
        tossResponse.setMId("dummy-mid");
        tossResponse.setReceiptUrl("http://dummy.receipt.url");

        ApiResult<TossPaymentResponseDto> dummyApiResult = ApiResult.success(tossResponse);

        webUtilMock.when(() -> WebUtil.postTossWithJson(
                contains("https://api.tosspayments.com/v1/billing/"),
                anyString(),
                any(PaymentPayRequestDto.class),
                eq(TossPaymentResponseDto.class)
        )).thenReturn(dummyApiResult);

        // Act
        PayResponseDto response = paymentService.pay(userDetails);

        // Assert
        assertTrue(response.getIsSuccess(), "Payment should be successful");
        assertEquals("200", response.getCode());
        assertEquals("ok", response.getMessage());

        assertEquals(1, paymentTransactionRepository.count(), "One payment transaction should be saved");

        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        assertTrue(optionalBilling.isPresent(), "Billing should be present");
        Billing updatedBilling = optionalBilling.get();
        assertEquals("ACTIVE", updatedBilling.getStatus().name());
    }

    @Test
    @DisplayName("Test pay: Throw an error if the end date has not yet passed.")
    void testPayFailure_NotPassEndAt() {
        // Arrange
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now().minusDays(1L))
                .build();
        billingRepository.save(billing);

        // Act
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            paymentService.pay(userDetails);
        });

        assertEquals("Impossible to call api::pay::user=" + user.getEmail(), runtimeException.getMessage());
    }

    @Test
    @DisplayName("Test pay: Handles user error from Toss API correctly")
    void testPayFailure_UserError() {
        // Arrange
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now().plusDays(1))
                .build();
        billingRepository.save(billing);

        TossFailDto.ErrorDetail errorDetail = TossFailDto.ErrorDetail.builder()
                .code("INVALID_REJECT_CARD")
                .message("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.")
                .build();
        TossFailDto tossFailDto = TossFailDto.builder()
                .error(errorDetail)
                .build();

        ApiResult<TossPaymentResponseDto> dummyApiResult = ApiResult.failure(tossFailDto);

        webUtilMock.when(() -> WebUtil.postTossWithJson(
                contains("https://api.tosspayments.com/v1/billing/"),
                anyString(),
                any(PaymentPayRequestDto.class),
                eq(TossPaymentResponseDto.class)
        )).thenReturn(dummyApiResult);

        PayResponseDto response = paymentService.pay(userDetails);

        // Assert
        assertFalse(response.getIsSuccess(), "Payment should fail due to user error");
        assertEquals("400", response.getCode());
        assertEquals("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.", response.getMessage());
    }


    @Test
    @DisplayName("Test pay: Handles unexpected error from Toss API correctly")
    void testPayFailure_unexpectedError() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .endedAt(LocalDate.now().plusDays(1))
                .build();
        billingRepository.save(billing);

        TossFailDto.ErrorDetail errorDetail = TossFailDto.ErrorDetail.builder()
                .code("JUST_ERROR_CODE")
                .message("JUST_ERROR_CODE")
                .build();
        TossFailDto tossFailDto = TossFailDto.builder()
                .error(errorDetail)
                .build();

        ApiResult<TossPaymentResponseDto> dummyApiResult = ApiResult.failure(tossFailDto);

        webUtilMock.when(() -> WebUtil.postTossWithJson(
                contains("https://api.tosspayments.com/v1/billing/"),
                anyString(),
                any(PaymentPayRequestDto.class),
                eq(TossPaymentResponseDto.class)
        )).thenReturn(dummyApiResult);

        PayResponseDto response = paymentService.pay(userDetails);

        // Assert
        assertFalse(response.getIsSuccess(), "Payment should fail due to unexpected error");
        assertEquals("500", response.getCode());
        assertEquals("예기치 못한 에러가 발생하였습니다. 문의 부탁드립니다.", response.getMessage());
    }

    @Test
    @DisplayName("plan-type test: request plan type changed")
    void testPlanTypeSuccess() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .billingKey("dummy-billing-key")
                .customerKey("dummy-customer-key")
                .price(BigDecimal.valueOf(9900))
                .planType(PlanType.MONTHLY)
                .startedAt(LocalDate.now())
                .renewal(false)
                .endedAt(LocalDate.now().plusMonths(1))
                .build();

        billingRepository.save(billing);

        BillingPlanTypeRequestDto billingPlanTypeRequestDto = new BillingPlanTypeRequestDto();
        billingPlanTypeRequestDto.setPlanType(PlanType.ANNUAL);

        //Act
        paymentService.changePlanType(userDetails, billingPlanTypeRequestDto);

        //Assert
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        assertTrue(optionalBilling.isPresent(), "Billing should be present");
        Billing updateBilling = optionalBilling.get();
        assertEquals(PlanType.ANNUAL, updateBilling.getPlanType());
        assertEquals(true, updateBilling.getRenewal());
    }

    @Test
    @DisplayName("plan-type test: if endAt don't exist when request")
    void testPlanTypeFail_endAtDontExist() {
        User user = createTestUser();
        CustomUserDetails userDetails = createDummyCustomUserDetails(user.getEmail());

        Billing billing = Billing.builder()
                .user(user)
                .build();
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        paymentInfoDto.setPlanType(PlanType.MONTHLY);
        paymentInfoDto.setPrice(BigDecimal.valueOf(9900));
        billing.changeInfo(paymentInfoDto);

        billingRepository.save(billing);

        BillingPlanTypeRequestDto billingPlanTypeRequestDto = new BillingPlanTypeRequestDto();
        billingPlanTypeRequestDto.setPlanType(PlanType.ANNUAL);

        //Act
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            paymentService.changePlanType(userDetails, billingPlanTypeRequestDto);
        });

        //Assert
        assertEquals("Impossible request::changePlanType::user=test@test.com", runtimeException.getMessage());
    }

    @Test
    @DisplayName("pay test: first pay test")
    void testFirstPaySuccess() {

    }

    @Test
    @DisplayName("pay test: pay after subscribe end")
    void testPaySuccess_afterSubscribeEnd() {

    }
}