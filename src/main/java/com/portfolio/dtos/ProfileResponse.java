package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private Integer id;
    private String fullName;
    private String title;
    private String aboutMe;
    private String email;
    private String phone;
    private String location;

    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;

}
