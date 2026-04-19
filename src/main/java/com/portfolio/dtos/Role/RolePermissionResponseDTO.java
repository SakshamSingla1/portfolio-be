package com.portfolio.dtos.Role;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.RoleStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RolePermissionResponseDTO extends AuditableResponse {
    private String id;
    private String name;
    private String description;
    private RoleStatusEnum status;
    private List<ModulePermissionDTO> navLinks;
}
