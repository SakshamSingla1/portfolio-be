package com.portfolio.dtos.Skill;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillStat {
    private Long expertSkillCount;
    private Long advancedSkillCount;
    private Long intermediateSkillCount;
    private Long beginnerSkillCount;
}
