package com.portfolio.dtos.ContactUs;

import com.portfolio.enums.ContactUsStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContactUsResponse {
    private String id;
    private String name;
    private String email;
    private String message;
    private String phone;
    private ContactUsStatusEnum status;
    private LocalDateTime createdAt;
}
