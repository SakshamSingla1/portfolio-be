package com.portfolio.dtos;

import lombok.Data;

@Data
public class PasswordRequest {
    String password;
    String otp;
}
