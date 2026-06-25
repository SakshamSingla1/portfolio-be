package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_views")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioView extends Auditable {

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

    // visitor metadata
    private String browser;
    private String os;
    private String language;
    private String timezone;
    private String country;
    private String city;

    @Column(name = "country_code")
    private String countryCode;
}
