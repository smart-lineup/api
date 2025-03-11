package com.jun.smartlineup.config.email;

import com.jun.smartlineup.user.domain.PasswordResetToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Async
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${frontend.url}")
    private String frontendBaseUrl;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String link = frontendBaseUrl + "/verify-email?token=" + token;
        String htmlContent = "<div style='font-family:Arial, sans-serif; line-height:1.6; color:#333;'>"
                + "<h2>안녕하세요,</h2>"
                + "<p>회원가입을 완료하려면 아래의 버튼을 클릭하여 이메일 인증을 진행해 주세요.</p>"
                + "<p>본 인증은 보안 강화를 위해 필요하며, 인증이 완료된 후 모든 기능을 이용하실 수 있습니다.</p>"
                + "<p style='text-align:center; margin:20px 0;'><a href='" + link + "' style='display:inline-block; padding:12px 24px; color:#fff; background-color:#007BFF; text-decoration:none; border-radius:5px;'>이메일 인증하기</a></p>"
                + "<p>만약 위 버튼이 작동하지 않는다면 아래의 링크를 클릭하시거나 복사하여 브라우저에 붙여넣어 주세요:</p>"
                + "<p><a href='" + link + "'>" + link + "</a></p>"
                + "<p>본 메일은 자동 발신 메일이므로 회신하지 말아 주세요.</p>"
                + "<p>감사합니다.<br>운영팀 드림</p>"
                + "</div>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[Smart Line Up] 이메일 인증 안내");
        helper.setText(htmlContent, true); // ✅ HTML 형식 활성화

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[Smart Line up] 비밀번호 재설정 인증 코드");

        String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                + "<h2 style='color: #007BFF;'>Smart Line up</h2>"
                + "<p>안녕하세요,</p>"
                + "<p>비밀번호 재설정을 위해 아래의 인증 코드를 입력해 주세요.</p>"
                + "<div style='margin: 20px 0; padding: 10px; background-color: #f0f0f0; border-radius: 5px; text-align: center; font-size: 1.2em; font-weight: bold;'>"
                + token
                + "</div>"
                + "<p>만약 본인이 요청하지 않은 경우, 이 메일을 무시하셔도 됩니다.</p>"
                + "<p>감사합니다.</p>"
                + "</div>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
