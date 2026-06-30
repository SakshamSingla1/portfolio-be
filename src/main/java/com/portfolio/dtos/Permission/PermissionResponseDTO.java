package com.portfolio.dtos.Permission;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionResponseDTO extends AuditableResponse {
    private Long id;
    private String name;

    public PermissionResponseDTO(Long id, String name,
                                 LocalDateTime createdAt, LocalDateTime updatedAt,
                                 Long createdBy, Long updatedBy,
                                 String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.name = name;
    }
}
