package com.portfolio.dtos;

import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponseDTO {
    private String id;
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private String message;
}
