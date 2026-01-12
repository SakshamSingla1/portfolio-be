package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginDTO {
    private String username;
    private String email;
    private String phone;
    private String otp;
    private String password;
}
