package com.portfolio.dtos.Landing;

import lombok.Data;

import java.util.List;

@Data
public class LandingHowToUseStepRequest {
    private String stepNumber;
    private String iconName;
    private String colorKey;
    private String title;
    private List<String> bullets;
    private int sortOrder;
    private boolean isActive;
}
