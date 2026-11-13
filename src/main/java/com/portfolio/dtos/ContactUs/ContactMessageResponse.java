package com.portfolio.dtos.ContactUs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ContactMessageResponse {

    private Long id;
    private String senderType;
    private String message;
    private LocalDateTime createdAt;
}
