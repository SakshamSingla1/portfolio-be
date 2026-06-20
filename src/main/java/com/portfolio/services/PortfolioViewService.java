package com.portfolio.services;

import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;

public interface PortfolioViewService {
    void trackView(PortfolioViewRequest request);
    ViewStatsDTO getViewStats(String profileId);
}
