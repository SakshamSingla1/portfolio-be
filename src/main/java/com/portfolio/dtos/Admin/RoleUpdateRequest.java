package com.portfolio.dtos.Admin;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class RoleUpdateRequest {
    private String role;
}
