package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityDTO {
    private String type;
    private String description;
    private LocalDateTime timestamp;
}
