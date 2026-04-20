package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneOtpRequestDTO {
    private String phone;
}
