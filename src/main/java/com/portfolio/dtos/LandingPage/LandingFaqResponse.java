package com.portfolio.dtos.LandingPage;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LandingFaqResponse extends AuditableResponse {
    private Long id;
    private String question;
    private String answer;
    private int sortOrder;
    private boolean isActive;
}
