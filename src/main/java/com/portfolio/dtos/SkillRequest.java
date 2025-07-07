package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.Data;

@Data
public class SkillRequest {
    private String name;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
}
