package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "landing_testimonials")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingTestimonial extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_role")
    private String authorRole;

    @Column(name = "author_company")
    private String authorCompany;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "sort_order")
    private int sortOrder;

    @Default
    @Column(name = "is_active")
    private boolean isActive = true;
}
