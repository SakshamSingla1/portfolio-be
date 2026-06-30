package com.portfolio.dtos.NavLinks;

import com.portfolio.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavLinkResponseDTO {
    private Long id;
    private String index;
    private String name;
    private String path;
    private String icon;
    private String navGroup;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String createdByName;
    private String updatedByName;
}
