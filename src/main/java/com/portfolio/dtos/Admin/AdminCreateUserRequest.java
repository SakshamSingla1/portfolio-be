package com.portfolio.dtos.Admin;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AdminCreateUserRequest {
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
    private String phone;
    @NotNull(message = "Role is required")
    private Long roleId;
    private StatusEnum status;
}
