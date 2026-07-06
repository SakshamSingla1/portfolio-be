package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;
    private String otp;
}
