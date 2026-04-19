package com.portfolio.dtos.Authentication;

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
    private String roleId;
    private String roleName;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private String message;
}
