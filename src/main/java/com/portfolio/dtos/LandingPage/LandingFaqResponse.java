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
public class LandingFaqResponse extends AuditableResponse {
    private Long id;
    private String question;
    private String answer;
    private int sortOrder;
    private boolean isActive;

    public LandingFaqResponse(Long id, String question, String answer, int sortOrder, boolean isActive,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               Long createdBy, Long updatedBy,
                               String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }
}
