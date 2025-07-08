package com.portfolio.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendPasswordResetEmail(String to,String name,String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink","http://localhost:5173/admin/reset-password?token="+token);
            String body = templateEngine.process("reset-password", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setText(body, true);
            
            mailSender.send(mimeMessage);
            
            System.out.println("Password reset email sent to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send password reset email to: " + to);
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error while sending password reset email: " + e.getMessage());
        }
    }
}
