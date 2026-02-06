package com.portfolio.servicesImpl;

import com.portfolio.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlContent) {
        System.out.println("JavaMailSender class 1 = " + mailSender.getClass().getName());
        try {
            System.out.println("JavaMailSender class 2 = " + mailSender.getClass().getName());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("JavaMailSender class 3 = " + mailSender.getClass().getName());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}
