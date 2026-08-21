package com.portfolio.dtos.Help;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HelpFaqResponse extends AuditableResponse {
    private Long id;
    private String question;
    private String answer;
    private int sortOrder;
    private boolean isActive;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }
}
