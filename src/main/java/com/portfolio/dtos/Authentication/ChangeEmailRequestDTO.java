package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeEmailRequestDTO {
    @NotBlank(message = "New email is required")
    @Email(message = "Enter a valid email")
    private String newEmail;
}
