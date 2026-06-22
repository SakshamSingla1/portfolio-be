package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "landing_features")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "color_key")
    private String colorKey;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sort_order")
    private int sortOrder;

    @Default
    @Column(name = "is_active")
    private boolean isActive = true;
}
