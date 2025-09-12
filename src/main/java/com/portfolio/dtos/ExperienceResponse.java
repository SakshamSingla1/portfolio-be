package com.portfolio.dtos;

import com.portfolio.dtos.Skill.SkillDropdown;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExperienceResponse {
    private int id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private boolean currentlyWorking;
    private String description;
    private List<SkillDropdown> technologiesUsed; // mapped skill list
}
