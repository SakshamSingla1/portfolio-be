package com.portfolio.dtos.NavLinks;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NavLinkResponseDTO {
    private String id;
    private String index;
    private String name;
    private String path;
    private String icon;
    private String navGroup;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
