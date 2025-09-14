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

    private static final String OTP_SUBJECT = "Your OTP Verification Code";
    private static final String OTP_TEMPLATE = "otp-verification";

    private static final String CONTACT_US_SUBJECT = "New Contact Us Message";
    private static final String CONTACT_US_TEMPLATE = "contact-us-message"; // 👈 file: resources/templates/contact-us-message.html

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOtpVerificationEmail(String to, String name, String otp) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp", otp);

            String body = templateEngine.process(OTP_TEMPLATE, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(OTP_SUBJECT);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            log.info("OTP email sent to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {} | Error: {}", to, e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String name, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", RESET_LINK_BASE_URL + token);

            String body = templateEngine.process(RESET_PASSWORD_TEMPLATE, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(RESET_PASSWORD_SUBJECT);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            log.info("Password reset email sent to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {} | Error: {}", to, e.getMessage());
        }
    }

    public void sendContactUsEmail(String to, String name, String email, String phone, String message) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("email", email);
            context.setVariable("phone", phone);
            context.setVariable("message", message);

            String body = templateEngine.process("new-contact-notification", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to); // ✅ portfolio owner’s email
            helper.setSubject("New Contact Us Message");
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            log.info("Contact Us email sent to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send Contact Us email to {} | Error: {}", to, e.getMessage());
        }
    }
}
