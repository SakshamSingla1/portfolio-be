package com.portfolio.dtos.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class RoleUpdateRequest {
    // Stringified role id — parsed with Long.parseLong in ProfileServiceImpl,
    // so it must be present and numeric or that call throws an unhandled
    // NumberFormatException instead of a clean validation error.
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "\\d+", message = "Role must be a valid role id")
    private String role;
}
