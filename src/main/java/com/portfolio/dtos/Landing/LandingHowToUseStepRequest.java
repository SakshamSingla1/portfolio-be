package com.portfolio.dtos.Landing;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class LandingHowToUseStepRequest {
    private String stepNumber;
    private String iconName;
    private String colorKey;
    @NotBlank(message = "Title is required")
    private String title;
    private List<String> bullets;
    private int sortOrder;
    private boolean isActive;
}
