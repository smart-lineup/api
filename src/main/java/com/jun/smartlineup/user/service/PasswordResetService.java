package com.jun.smartlineup.user.service;

import com.jun.smartlineup.config.email.EmailService;
import com.jun.smartlineup.common.exception.NoExistUserException;
import com.jun.smartlineup.user.domain.PasswordResetToken;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.ChangePasswordDto;
import com.jun.smartlineup.user.repository.PasswordResetTokenRepository;
import com.jun.smartlineup.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String generateResetToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    public void sendResetEmail(String email) throws MessagingException {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        String token = generateResetToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .build();
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email, token);
    }

    public boolean verifyToken(String email, String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (resetTokenOpt.isEmpty() ||
                resetTokenOpt.get().getIsUsed() ||
                !resetTokenOpt.get().getToken().equals(token)) {
            return false;
        }
        PasswordResetToken resetToken = resetTokenOpt.get();

        return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void changePassword(ChangePasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(NoExistUserException::new);

        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findTopByEmailOrderByCreatedAtDesc(dto.getEmail());
        if (resetTokenOpt.isEmpty() ||
                resetTokenOpt.get().getIsUsed() ||
                !resetTokenOpt.get().getToken().equals(dto.getToken())) {
            throw new RuntimeException("No Valid Error");
        }

        PasswordResetToken passwordResetToken = resetTokenOpt.get();
        passwordResetToken.use();

        user.changePassword(encodePassword(dto.getPassword()));
        userRepository.save(user);
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
