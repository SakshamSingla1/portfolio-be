package com.portfolio.dtos.logo;

import com.portfolio.enums.SkillCategoryEnum;
import lombok.*;

@Data
@Builder
public class LogoDropdown {
    private Integer id;
    private String name;
    private String url;
    private SkillCategoryEnum category;
}
