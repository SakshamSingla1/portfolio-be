package com.portfolio.dtos.Landing;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LandingTestimonialRequest {
    @NotBlank(message = "Author name is required")
    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String avatarUrl;
    @NotBlank(message = "Content is required")
    private String content;
    private String linkedinUrl;
    private int sortOrder;
    private boolean isActive;
}
