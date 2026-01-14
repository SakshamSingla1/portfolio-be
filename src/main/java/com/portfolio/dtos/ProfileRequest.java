package com.portfolio.dtos;

import lombok.Data;

@Data
public class ProfileRequest {
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
    private String profileImagePublicId;

    private String logoUrl;
    private String logoPublicId;


}
