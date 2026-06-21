package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "testimonials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Testimonial extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    private String name;
    private String role;
    private String company;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String imageUrl;
    private Long imageId;
    private String linkedInUrl;

    @Column(name = "sort_order")
    private String order;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
