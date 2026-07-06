package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TotpCodeDTO {
    @NotBlank(message = "TOTP code is required")
    private String totpCode;
}
