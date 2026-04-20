package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyEmailChangeDTO {
    private String newEmail;
    private String otp;
}
