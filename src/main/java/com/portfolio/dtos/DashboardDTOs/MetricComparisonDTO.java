package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricComparisonDTO {
    private long current;
    private long previous;
    // Null when `previous` is 0 — a quiet prior period and a range that predates any
    // data are indistinguishable, and both should read as "no comparison available"
    // rather than a misleading +Infinity%.
    private Double percentChange;
}
