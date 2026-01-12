package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDTO {
    private String oldPassword;   // Current password (for verification)
    private String newPassword;   // New password user wants to set
    private String confirmPassword; // Optional — for frontend confirmation check
}
