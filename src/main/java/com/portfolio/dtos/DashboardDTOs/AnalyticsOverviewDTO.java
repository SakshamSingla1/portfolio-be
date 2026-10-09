package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsOverviewDTO {
    private AnalyticsRangeDTO range;
    private AnalyticsComparisonDTO comparison;
    // One point per day spanning exactly the selected range.
    private List<DailyViewDTO> trend;
    private Map<String, Long> deviceBreakdown;
    private Map<String, Long> browserBreakdown;
    private Map<String, Long> locationBreakdown;
    private Map<String, Long> referrerBreakdown;
    // Capped at 200, within the selected range.
    private List<PortfolioViewDTO> recentViews;
    // Always the trailing 90 days, independent of the selected range.
    private List<DailyViewDTO> viewsHeatmap;
}
