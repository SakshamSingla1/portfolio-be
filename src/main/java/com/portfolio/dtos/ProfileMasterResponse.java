package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileMasterResponse {

    private ProfileResponse profile;      // use ProfileResponse DTO

    private List<ProjectResponse> projects;
    private List<ExperienceResponse> experiences;
    private List<EducationResponse> educations;
    private List<SkillResponse> skills;
}
