package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRegisterDTO {
    private String userName;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String phone;
}
