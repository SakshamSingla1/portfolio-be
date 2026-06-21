package com.portfolio.services;

import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;

public interface DashboardService {
    DashboardSummaryDTO getDashboardSummary(Long profileId);
}
