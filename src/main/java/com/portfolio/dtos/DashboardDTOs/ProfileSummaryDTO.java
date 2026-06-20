package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileSummaryDTO {
    private String fullName;
    private String title;
    private String location;
    private String profileImageUrl;
}
