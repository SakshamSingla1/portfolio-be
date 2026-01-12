package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoResponse {
    private String id;
    private String name;
    private String url;
    private SkillCategoryEnum category;
}
