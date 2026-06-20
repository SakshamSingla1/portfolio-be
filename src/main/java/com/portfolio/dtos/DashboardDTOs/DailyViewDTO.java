package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyViewDTO {
    private String day;   // Mon, Tue …
    private String date;  // Jun 18
    private long count;
}
