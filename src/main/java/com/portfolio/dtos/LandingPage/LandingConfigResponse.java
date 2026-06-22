package com.portfolio.dtos.LandingPage;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LandingConfigResponse extends AuditableResponse {
    private Long id;
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
