package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyEmailChangeDTO {
    @NotBlank(message = "New email is required")
    @Email(message = "Enter a valid email")
    private String newEmail;
    @NotBlank(message = "OTP is required")
    private String otp;
}
