package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "otp_store")
public class OtpStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}
