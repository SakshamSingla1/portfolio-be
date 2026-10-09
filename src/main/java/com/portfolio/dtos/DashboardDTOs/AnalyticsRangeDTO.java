package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AnalyticsRangeDTO {
    private String key;        // 7d, 30d, 90d, custom
    private LocalDate startDate;
    private LocalDate endDate;
    private String label;      // e.g. "Last 30 days"
}
