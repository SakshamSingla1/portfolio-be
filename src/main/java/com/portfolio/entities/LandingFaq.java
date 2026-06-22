package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "landing_faqs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "sort_order")
    private int sortOrder;

    @Default
    @Column(name = "is_active")
    private boolean isActive = true;
}
