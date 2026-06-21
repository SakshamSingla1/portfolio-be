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
@Table(name = "achievements")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievements extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;

    @Column(name = "sort_order")
    private String order;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
