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
    private String profileImageUrl;
    private String profileImagePublicId;
    private String aboutMeImageUrl;
    private String aboutMeImagePublicId;
    private String logoUrl;
    private String logoPublicId;
    private String themeName;
}
