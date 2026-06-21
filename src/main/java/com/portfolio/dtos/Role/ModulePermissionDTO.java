package com.portfolio.dtos.Role;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ModulePermissionDTO {
    private Long navLinkId;
    private String name;
    private String path;
    private String navGroup;
    private String index;
    private List<PermissionDTO> permissions;
}
