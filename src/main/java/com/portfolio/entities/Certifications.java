package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certifications extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    private String title;
    private String issuer;
    private String credentialId;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "sort_order")
    private Integer order;
}
