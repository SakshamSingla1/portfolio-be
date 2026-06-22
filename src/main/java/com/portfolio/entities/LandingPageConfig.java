package com.portfolio.entities;

import com.portfolio.converters.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "landing_page_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingPageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hero_eyebrow")
    private String heroEyebrow;

    @Column(name = "hero_headline_1")
    private String heroHeadline1;

    @Column(name = "hero_headline_2")
    private String heroHeadline2;

    @Column(name = "hero_description", columnDefinition = "TEXT")
    private String heroDescription;

    @Column(name = "hero_primary_cta_text")
    private String heroPrimaryCtaText;

    @Column(name = "hero_secondary_cta_text")
    private String heroSecondaryCtaText;

    @Convert(converter = StringListConverter.class)
    @Column(name = "hero_trust_badges", columnDefinition = "TEXT")
    private List<String> heroTrustBadges;

    @Column(name = "cta_badge_text")
    private String ctaBadgeText;

    @Column(name = "cta_headline")
    private String ctaHeadline;

    @Column(name = "cta_description", columnDefinition = "TEXT")
    private String ctaDescription;

    @Column(name = "cta_button_text")
    private String ctaButtonText;

    @Convert(converter = StringListConverter.class)
    @Column(name = "cta_trust_points", columnDefinition = "TEXT")
    private List<String> ctaTrustPoints;
}
