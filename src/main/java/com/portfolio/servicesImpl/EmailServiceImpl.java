package com.portfolio.servicesImpl;

import com.portfolio.services.EmailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final Resend resend;

    @Value("${resend.from-email:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            CreateEmailOptions createEmailOptions = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(createEmailOptions);
            log.info("Email sent successfully via Resend. ID: {}", response.getId());
        } catch (Exception e) {
            log.error("Failed to send email via Resend to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email via Resend: " + e.getMessage(), e);
        }
    }
}


