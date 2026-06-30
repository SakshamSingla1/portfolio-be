package com.portfolio.dtos.Role;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.RoleStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleListResponseDTO extends AuditableResponse {
    private Long id;
    private String name;
    private String description;
    private RoleStatusEnum status;
    private List<RoleMappedModule> roleMappedModules;

    public RoleListResponseDTO(Long id, String name, RoleStatusEnum status,
                               String description, LocalDateTime createdAt,
                               Long createdBy, LocalDateTime updatedAt,
                               Long updatedBy, String createdByName,
                               String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }
}
