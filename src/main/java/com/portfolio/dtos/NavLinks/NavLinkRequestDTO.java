package com.portfolio.dtos.NavLinks;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

@Data
public class NavLinkRequestDTO {
    private String index;
    private String name;
    private String path;
    private StatusEnum status;
}
