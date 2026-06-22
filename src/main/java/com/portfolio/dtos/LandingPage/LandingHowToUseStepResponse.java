package com.portfolio.dtos.LandingPage;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LandingHowToUseStepResponse extends AuditableResponse {
    private Long id;
    private String stepNumber;
    private String iconName;
    private String colorKey;
    private String title;
    private List<String> bullets;
    private int sortOrder;
    private boolean isActive;
}
