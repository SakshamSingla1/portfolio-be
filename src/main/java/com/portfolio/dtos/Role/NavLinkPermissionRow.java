package com.portfolio.dtos.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavLinkPermissionRow {
    private Long navLinkId;
    private String navLinkName;
    private String path;
    private String navGroup;
    private String index;
    private Long permissionId;
    private String permissionName;
}
