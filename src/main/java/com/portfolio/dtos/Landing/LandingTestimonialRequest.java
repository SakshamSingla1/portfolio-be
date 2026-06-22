package com.portfolio.dtos.Landing;

import lombok.Data;

@Data
public class LandingTestimonialRequest {
    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String avatarUrl;
    private String content;
    private String linkedinUrl;
    private int sortOrder;
    private boolean isActive;
}
