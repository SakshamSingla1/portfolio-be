package com.portfolio.dtos.LandingPage;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LandingTestimonialResponse extends AuditableResponse {
    private Long id;
    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String avatarUrl;
    private String content;
    private String linkedinUrl;
    private int sortOrder;
    private boolean isActive;
}
