package com.portfolio.dtos.NavLinks;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NavLinkRequestDTO {
    private String index;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Path is required")
    private String path;
    private String icon;
    private String navGroup;
    private StatusEnum status;
}
