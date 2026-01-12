package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetConfirmDTO {
    private String token;
    private String newPassword;
}
