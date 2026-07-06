package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "social_links")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialLinks extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;

    private String url;

    @Column(name = "sort_order")
    private Integer order;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
