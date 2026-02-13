package com.portfolio.dtos;

import com.portfolio.dtos.ProjectImages.ProjectImageRequest;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.enums.WorkStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectResponse {

    private String id;
    private String projectName;
    private String projectDescription;
    private List<String> githubRepositories;
    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private WorkStatusEnum workStatus;
    private List<SkillDropdown> skills;
    private List<ProjectImageRequest> projectImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
