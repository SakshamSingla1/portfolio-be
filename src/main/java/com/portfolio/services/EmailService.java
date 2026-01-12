package com.portfolio.services;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlContent);
}
