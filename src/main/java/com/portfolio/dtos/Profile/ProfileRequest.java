package com.portfolio.dtos.Profile;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Username is required")
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
}
