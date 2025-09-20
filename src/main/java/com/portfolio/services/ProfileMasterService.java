package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Profile;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileMasterService {

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final SkillService skillService;

    public ProfileMasterService(
                                ProjectService projectService,
                                ExperienceService experienceService,
                                EducationService educationService,
                                SkillService skillService, ProfileService profileService) {
        this.projectService = projectService;
        this.experienceService = experienceService;
        this.educationService = educationService;
        this.skillService = skillService;
        this.profileService = profileService;
    }
    public ProfileMasterResponse getProfileMasterData(Integer profileId, int page, int size, String search) throws GenericException {
        ProfileResponse profileResponse = profileService.get(profileId); // new service method to return ProfileResponse
        List<ProjectResponse> projects = projectService.getProjectByProfileId(profileId);
        List<ExperienceResponse> experiences = experienceService.getExperienceByProfileId(profileId);
        List<EducationResponse> educations = educationService.getEducationByProfileId(profileId);
        List<SkillResponse> skills = skillService.getSkillByProfileId(profileId);

        return ProfileMasterResponse.builder()
                .profile(profileResponse)
                .projects(projects)
                .experiences(experiences)
                .educations(educations)
                .skills(skills)
                .build();
    }
}