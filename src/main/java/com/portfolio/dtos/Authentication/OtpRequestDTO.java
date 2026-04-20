package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpRequestDTO {
    private String email;
    private String otp;
}
