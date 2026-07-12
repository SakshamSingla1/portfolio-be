package com.portfolio.dtos.Language;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.LanguageProficiencyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileLanguageResponse extends AuditableResponse {
    private Long id;
    private String languageName;
    private LanguageProficiencyEnum proficiency;
    private Integer sortOrder;
}
