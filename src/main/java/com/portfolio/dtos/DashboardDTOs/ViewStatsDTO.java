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
    private long viewsLastWeek;
    private long viewsThisMonth;
    private long uniqueVisitors;
    private long resumeDownloads;
    private List<DailyViewDTO> weeklyTrend;
    // Same shape as weeklyTrend but covering ~90 days, for the GitHub-style
    // views-heatmap calendar (which needs more history than the 7-day trend).
    private List<DailyViewDTO> viewsHeatmap;
    private Map<String, Long> deviceBreakdown;
    private Map<String, Long> browserBreakdown;
    private Map<String, Long> locationBreakdown;
    private Map<String, Long> referrerBreakdown;
    private List<PortfolioViewDTO> recentViews;
}
