package com.portfolio.dtos.Logos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoDropdown {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
