package com.portfolio.dtos.DashboardDTOs;

import com.portfolio.dtos.ContactUsResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardSummaryDTO {
    private StatsDTO stats;
    private ProfileCompletionDTO profileCompletion;
    private List<ContactUsResponse> recentMessages;
    private List<ActivityDTO> recentActivities;
}
