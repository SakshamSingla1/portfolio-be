package com.portfolio.dtos;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.enums.EmploymentStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExperienceResponse {
    private String id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<SkillDropdown> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
