package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PortfolioViewDTO {
    private String device;
    private String referrer;
    private LocalDateTime timestamp;
    private String sessionId;   // first 8 chars — used as a display fingerprint
}
