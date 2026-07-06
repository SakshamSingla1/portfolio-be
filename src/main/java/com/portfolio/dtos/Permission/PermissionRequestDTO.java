package com.portfolio.dtos.Permission;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PermissionRequestDTO {
    @NotBlank(message = "Permission name is required")
    private String name;
}
