package com.portfolio.dtos.User;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends AuditableResponse {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private String role;
    private Long roleId;
    private String roleName;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private String profileImageUrl;
}
