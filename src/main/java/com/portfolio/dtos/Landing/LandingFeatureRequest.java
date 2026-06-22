package com.portfolio.dtos.Landing;

import lombok.Data;

@Data
public class LandingFeatureRequest {
    private String iconName;
    private String colorKey;
    private String title;
    private String description;
    private int sortOrder;
    private boolean isActive;
}
