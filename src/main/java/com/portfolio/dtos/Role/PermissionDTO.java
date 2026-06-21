package com.portfolio.dtos.Role;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class PermissionDTO {
    private Long id;
    private String name;
}
