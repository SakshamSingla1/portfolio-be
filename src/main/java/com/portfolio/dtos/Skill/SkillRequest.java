package com.portfolio.dtos.Skill;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    private Long profileId;
    @NotNull(message = "Logo is required")
    private Long logoId;
    @NotNull(message = "Skill level is required")
    private SkillLevelEnum level;
    @NotNull(message = "Skill category is required")
    private SkillCategoryEnum category;
}
