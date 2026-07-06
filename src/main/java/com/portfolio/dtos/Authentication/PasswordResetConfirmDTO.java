package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetConfirmDTO {
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
