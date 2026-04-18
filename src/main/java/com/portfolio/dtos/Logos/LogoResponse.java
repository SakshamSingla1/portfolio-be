package com.portfolio.dtos.Logos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogoResponse {
    private String id;
    private String name;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
