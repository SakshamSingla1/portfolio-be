package com.portfolio.dtos.Profile;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileResponse extends AuditableResponse {

    private String id;
    private String fullName;
    private String userName;
    private String title;
    private String aboutMe;
    private String email;
    private String phone;
    private String location;

    private String profileImageUrl;
    private String profileImagePublicId;

    private String aboutMeImageUrl;
    private String aboutMeImagePublicId;

    private String logoUrl;
    private String logoPublicId;

    private String themeName;

    private String roleId;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
}
