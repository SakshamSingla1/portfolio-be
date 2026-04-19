package com.portfolio.dtos.Permission;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PermissionResponseDTO extends AuditableResponse {
    private String id;
    private String name;
}
