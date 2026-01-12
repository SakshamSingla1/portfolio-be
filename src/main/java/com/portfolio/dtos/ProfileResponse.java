package com.portfolio.dtos;

import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProfileResponse {

    private String id;
    private String fullName;
    private String userName;
    private String title;
    private String aboutMe;
    private String email;
    private String phone;
    private String location;

    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
    private String profileImageUrl;
    private String logoUrl;

    private String role;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
