package com.portfolio.dtos.Skill;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    private Long profileId;
    private Long logoId;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
}
