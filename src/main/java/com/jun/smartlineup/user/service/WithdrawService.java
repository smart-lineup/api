package com.jun.smartlineup.user.service;

import com.jun.smartlineup.attendee.repository.AttendeeRepository;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.repository.BillingRepository;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawService {
    private final UserRepository userRepository;
    private final BillingRepository billingRepository;
    private final LineRepository lineRepository;
    private final AttendeeRepository attendeeRepository;
    private final QueueRepository queueRepository;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void withdraw(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        log.info("[WITHDRAW] Start withdraw process for user: {}", user.getEmail());
        Optional<Billing> optionalBilling = billingRepository.getBillingByUser(user);
        if (optionalBilling.isPresent()) {
            Billing billing = optionalBilling.get();
            billing.deleteBilling();
            log.info("[WITHDRAW] Billing deleted for user: {}", user.getEmail());
        }

        queueRepository.deleteAllByUser(user);
        log.info("[WITHDRAW] Queues deleted for user: {}", user.getEmail());

        lineRepository.deleteAllByUser(user);
        log.info("[WITHDRAW] Lines deleted for user: {}", user.getEmail());

        attendeeRepository.deleteAllByUser(user);
        log.info("[WITHDRAW] Attendees deleted for user: {}", user.getEmail());

        user.deleteUser();
        log.info("[WITHDRAW] User marked as deleted: {}", user.getEmail());
    }
}
