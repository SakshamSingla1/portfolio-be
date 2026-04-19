package com.portfolio.dtos.Role;

import com.portfolio.enums.RoleStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequestBodyDTO {
    private String name;
    private String description;
    private RoleStatusEnum status;
    private List<RolePermissionRequestDTO> rolePermissions;
}
