package com.portfolio.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String userName;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String aboutMe;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String location;
    private String password;
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatusEnum emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatusEnum phoneVerified;

    private boolean isTwoFactorEnabled;
    private String totpSecret;


    private boolean isAvailableForWork;
    private String availabilityNote;
    private LocalDate availableFrom;

    @Column(name = "is_discoverable")
    private boolean isDiscoverable;

    @Column(name = "digest_email_enabled")
    private boolean digestEmailEnabled;

    @Column(name = "digest_last_sent_at")
    private LocalDateTime digestLastSentAt;
}
