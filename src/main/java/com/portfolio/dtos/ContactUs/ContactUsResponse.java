package com.portfolio.dtos.ContactUs;

import com.portfolio.enums.ContactUsStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContactUsResponse {
    private Long id;
    private String name;
    private String email;
    private String message;
    private String phone;
    private ContactUsStatusEnum status;
    private LocalDateTime createdAt;
    private String replyMessage;
    private LocalDateTime repliedAt;

    public ContactUsResponse(Long id, String name,
                             String email, String message,
                             String phone, ContactUsStatusEnum status,
                             LocalDateTime createdAt, String replyMessage,
                             LocalDateTime repliedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.replyMessage = replyMessage;
        this.repliedAt = repliedAt;
    }
}
