package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorSetupResponseDTO {
    private String secret;
    private String otpAuthUrl;
}
