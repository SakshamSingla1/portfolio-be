package com.portfolio.dtos.NavLinks;

import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class NavLinkRequestDTO {
    private String index;
    private String name;
    private String path;
    private String icon;
    private List<String> roles;
    private StatusEnum status;
}
