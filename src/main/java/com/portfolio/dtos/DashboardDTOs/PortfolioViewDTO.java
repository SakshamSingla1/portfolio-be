package com.portfolio.dtos.DashboardDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PortfolioViewDTO {
    private String device;
    private String referrer;
    // Serialized with explicit Z so browsers parse it as UTC
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    private String sessionId;
    private String browser;
    private String os;
    private String language;
    private String timezone;
    private String country;
    private String city;
    private String countryCode;
}
