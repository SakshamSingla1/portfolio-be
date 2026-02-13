package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileCompletionDTO {
    private int percentage;
    private List<String> missingSections;
}
