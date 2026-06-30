package com.portfolio.dtos.User;

import java.time.LocalDateTime;

import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private Long roleId;
    private String roleName;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String createdByName;
    private String updatedByName;
}
