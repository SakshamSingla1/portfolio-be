package com.portfolio.dtos.Language;

import com.portfolio.enums.LanguageProficiencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileLanguageRequest {
    private Long profileId;

    @NotBlank(message = "Language name is required")
    private String languageName;

    @NotNull(message = "Proficiency is required")
    private LanguageProficiencyEnum proficiency;

    private Integer sortOrder;
}
