package com.portfolio.dtos.LandingPage;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LandingAudienceCardResponse extends AuditableResponse {
    private Long id;
    private String iconName;
    private String colorKey;
    private String title;
    private String description;
    private int sortOrder;
    private boolean isActive;
}
