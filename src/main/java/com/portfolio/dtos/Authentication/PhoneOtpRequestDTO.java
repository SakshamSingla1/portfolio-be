package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneOtpRequestDTO {
    @NotBlank(message = "Phone number is required")
    private String phone;
}
