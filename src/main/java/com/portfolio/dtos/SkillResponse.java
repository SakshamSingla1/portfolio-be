package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillResponse {
    private int id;
    private String name;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
}
