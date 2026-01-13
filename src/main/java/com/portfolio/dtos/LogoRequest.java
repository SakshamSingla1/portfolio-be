package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import lombok.Data;

@Data
public class LogoRequest {
    private String name;
    private String url;
    private SkillCategoryEnum category;
}
