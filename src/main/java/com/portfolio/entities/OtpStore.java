package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_store")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}
