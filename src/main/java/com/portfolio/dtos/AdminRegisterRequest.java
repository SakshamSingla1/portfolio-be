package com.portfolio.dtos;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}
