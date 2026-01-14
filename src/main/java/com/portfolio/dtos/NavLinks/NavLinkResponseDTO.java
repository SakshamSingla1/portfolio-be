package com.portfolio.dtos.NavLinks;

import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NavLinkResponseDTO {
    private String id;
    private String index;
    private String name;
    private String path;
    private String icon;
    private RoleEnum role;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
