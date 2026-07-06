package com.portfolio.dtos.Role;

import com.portfolio.enums.RoleStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequestBodyDTO {
    @NotBlank(message = "Role name is required")
    private String name;
    private String description;
    private RoleStatusEnum status;
    private List<RolePermissionRequestDTO> rolePermissions;
}
