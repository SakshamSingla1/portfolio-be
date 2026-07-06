package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TwoFactorVerifyDTO {
    @NotBlank(message = "Pending token is required")
    private String pendingToken;
    @NotBlank(message = "TOTP code is required")
    private String totpCode;
}
