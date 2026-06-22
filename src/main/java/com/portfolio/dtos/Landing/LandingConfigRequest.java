package com.portfolio.dtos.Landing;

import lombok.Data;

import java.util.List;

@Data
public class LandingConfigRequest {
    private String heroEyebrow;
    private String heroHeadline1;
    private String heroHeadline2;
    private String heroDescription;
    private String heroPrimaryCtaText;
    private String heroSecondaryCtaText;
    private List<String> heroTrustBadges;
    private String ctaBadgeText;
    private String ctaHeadline;
    private String ctaDescription;
    private String ctaButtonText;
    private List<String> ctaTrustPoints;
}
