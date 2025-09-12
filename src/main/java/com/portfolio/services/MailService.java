package com.portfolio.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String RESET_PASSWORD_SUBJECT = "Password Reset Request";
    private static final String RESET_PASSWORD_TEMPLATE = "reset-password";
    private static final String RESET_LINK_BASE_URL = "http://localhost:5173/admin/reset-password?token=";

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOtpVerificationEmail(String to, String name, String otp) {
        try {
            // Prepare the email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp", otp);

            // Template name (you'll create this template file in resources/templates)
            String body = templateEngine.process("otp-verification", context);

            // Create MIME email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject("Your OTP Verification Code");
            helper.setText(body, true); // HTML content

            mailSender.send(mimeMessage);
            log.info("OTP email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {} | Error: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending OTP email to: {} | Error: {}", to, e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String name, String token) {
        try {
            // Prepare email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", RESET_LINK_BASE_URL + token);

            String body = templateEngine.process(RESET_PASSWORD_TEMPLATE, context);

            // Create MIME email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(RESET_PASSWORD_SUBJECT);
            helper.setText(body, true); // HTML content

            // Send the email
            mailSender.send(mimeMessage);
            log.info("Password reset email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {} | Error: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending password reset email to: {} | Error: {}", to, e.getMessage());
        }
    }
}
