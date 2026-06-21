package com.portfolio.dtos.Skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SkillDropdown {
    private Long id;
    private String logoName;
    private String logoUrl;
}
