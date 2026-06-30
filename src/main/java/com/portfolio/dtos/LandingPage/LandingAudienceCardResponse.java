package com.portfolio.dtos.LandingPage;

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
public class LandingAudienceCardResponse extends AuditableResponse {
    private Long id;
    private String iconName;
    private String colorKey;
    private String title;
    private String description;
    private int sortOrder;
    private boolean isActive;

    public LandingAudienceCardResponse(Long id, String iconName, String colorKey, String title, String description,
                                        int sortOrder, boolean isActive,
                                        LocalDateTime createdAt, LocalDateTime updatedAt,
                                        Long createdBy, Long updatedBy,
                                        String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.iconName = iconName;
        this.colorKey = colorKey;
        this.title = title;
        this.description = description;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }
}
