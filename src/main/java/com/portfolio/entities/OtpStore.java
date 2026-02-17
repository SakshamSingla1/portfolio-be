package com.portfolio.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otp_store")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpStore {

    @Id
    private String id;

    @Indexed
    private String profileId;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}

