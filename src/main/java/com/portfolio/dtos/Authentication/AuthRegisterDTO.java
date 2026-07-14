package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRegisterDTO {
    @NotBlank(message = "Username is required")
    private String userName;
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$", message = "Password must contain at least one uppercase letter and one digit")
    private String password;
    private String role;
    private String phone;
}
