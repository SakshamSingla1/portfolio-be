package com.portfolio.dtos;

import com.portfolio.dtos.Skill.SkillDropdown;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectResponse {
    private Integer id;
    private String projectName;
    private String projectDescription;
    private String projectLink;
    private List<SkillDropdown> technologiesUsed; // mapped skill list
    private Date projectStartDate;
    private Date projectEndDate;
    private boolean currentlyWorking;
    private String projectImageUrl;
}
