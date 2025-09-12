package com.portfolio.dtos;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phone;
    private String otp;
    private String newEmail;
}
