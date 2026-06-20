package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ViewStatsDTO {
    private long totalViews;
    private long viewsToday;
    private long viewsThisWeek;
    private long viewsThisMonth;
    private long uniqueVisitors;
    private long resumeDownloads;
    private List<DailyViewDTO> weeklyTrend;
    private Map<String, Long> deviceBreakdown;
    private List<PortfolioViewDTO> recentViews;
}
