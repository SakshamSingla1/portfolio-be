package com.portfolio.dtos.DashboardDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PortfolioViewRequest {
    @NotNull @Positive
    private Long profileId;
    private String sessionId;
    private String device;
    private String referrer;
    private String path;
    // enriched fields sent by the browser
    private String browser;
    private String os;
    private String language;
    private String timezone;
}
