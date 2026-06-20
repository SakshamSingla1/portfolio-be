package com.portfolio.dtos.DashboardDTOs;

import lombok.Data;

@Data
public class PortfolioViewRequest {
    private String profileId;
    private String sessionId;
    private String device;    // DESKTOP | MOBILE | TABLET
    private String referrer;
    private String path;
}
