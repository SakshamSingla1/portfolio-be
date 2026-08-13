package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileCompletionDTO {
    private int percentage;
    private List<String> missingSections;
    // Last 30 days of daily snapshots, oldest first, so the dashboard can chart
    // the score trending up rather than only showing today's value.
    private List<CompletionSnapshotDTO> trend;
}
