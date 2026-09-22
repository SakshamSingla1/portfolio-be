package com.portfolio.dtos.SubscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read-only shape for the public landing page's pricing section — deliberately
 * excludes audit fields and raw nav-link IDs (SubscriptionPlanResponseDTO
 * carries both) since this is served without authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanPublicResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal priceMonthly;
    private BigDecimal priceYearly;
    private String currency;
    private boolean isDefault;
    private List<String> highlights;
}
