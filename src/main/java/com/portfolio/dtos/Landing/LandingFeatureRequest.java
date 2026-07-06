package com.portfolio.dtos.Landing;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LandingFeatureRequest {
    private String iconName;
    private String colorKey;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private int sortOrder;
    private boolean isActive;
}
