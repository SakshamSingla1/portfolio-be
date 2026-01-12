package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpRequestDTO {
    private String email;
    private String otp;
}
