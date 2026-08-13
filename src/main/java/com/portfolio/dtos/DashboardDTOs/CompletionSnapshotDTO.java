package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompletionSnapshotDTO {
    private String date;   // Jun 18
    private int percentage;
}
