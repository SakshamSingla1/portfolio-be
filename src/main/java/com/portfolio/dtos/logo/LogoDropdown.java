package com.portfolio.dtos.logo;

import com.portfolio.enums.SkillCategoryEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoDropdown {
    private String id;
    private String name;
    private String url;
    private SkillCategoryEnum category;
}
