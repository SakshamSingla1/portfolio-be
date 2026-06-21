package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_views")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "session_id")
    private String sessionId;

    private String device;
    private String referrer;
    private LocalDateTime timestamp;
}
