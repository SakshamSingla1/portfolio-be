package com.portfolio.dtos.Admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class RoleUpdateRequest {
    private String role;
}
