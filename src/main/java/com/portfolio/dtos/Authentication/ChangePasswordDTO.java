package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDTO {
    @NotBlank(message = "Current password is required")
    private String oldPassword;   // Current password (for verification)
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$", message = "Password must contain at least one uppercase letter and one digit")
    private String newPassword;   // New password user wants to set
    private String confirmPassword; // Optional — for frontend confirmation check
}
