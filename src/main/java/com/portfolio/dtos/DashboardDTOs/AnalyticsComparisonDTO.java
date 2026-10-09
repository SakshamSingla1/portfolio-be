package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsComparisonDTO {
    private MetricComparisonDTO views;
    private MetricComparisonDTO uniqueVisitors;
    private MetricComparisonDTO resumeDownloads;
}
