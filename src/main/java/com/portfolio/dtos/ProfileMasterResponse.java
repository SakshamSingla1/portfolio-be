package com.portfolio.dtos;

import com.portfolio.dtos.EducationResponse;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class ProfileMasterResponse {

    private ProfileResponse profile;      // use ProfileResponse DTO

    private Page<ProjectResponse> projects;
    private Page<ExperienceResponse> experiences;
    private Page<EducationResponse> educations;
    private Page<SkillResponse> skills;
}
