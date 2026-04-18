package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetRequestDTO {
    private String email;
}
