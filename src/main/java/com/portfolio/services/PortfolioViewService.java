package com.portfolio.services;

import com.portfolio.dtos.DashboardDTOs.AnalyticsOverviewDTO;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;

public interface PortfolioViewService {
    void trackView(PortfolioViewRequest request, String clientIp, String userAgent);
    ViewStatsDTO getViewStats(Long profileId);
    AnalyticsOverviewDTO getViewStatsForRange(Long profileId, String rangeKey, String startDateParam, String endDateParam);
}
