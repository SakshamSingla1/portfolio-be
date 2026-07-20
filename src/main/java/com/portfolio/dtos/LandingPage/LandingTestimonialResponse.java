package com.portfolio.dtos.LandingPage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
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

    public LandingTestimonialResponse(Long id, String authorName, String authorRole, String authorCompany,
                                       String avatarUrl, String content, String linkedinUrl,
                                       int sortOrder, boolean isActive,
                                       LocalDateTime createdAt, LocalDateTime updatedAt,
                                       Long createdBy, Long updatedBy,
                                       String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.authorCompany = authorCompany;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.linkedinUrl = linkedinUrl;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }
}
