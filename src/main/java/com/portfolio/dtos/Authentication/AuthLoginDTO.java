package com.portfolio.dtos.Authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginDTO {
    private String username;

    @Email(message = "Must be a valid email address")
    private String email;

    private String phone;
    private String otp;

    @Size(min = 1, message = "Password must not be blank")
    private String password;
}
