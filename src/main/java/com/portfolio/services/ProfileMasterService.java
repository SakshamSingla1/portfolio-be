package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ProfileRepository profileRepository;

    public ProfileMasterService(
                                ProjectService projectService,
                                ExperienceService experienceService,
                                EducationService educationService,
                                SkillService skillService,
                                ProfileService profileService,
                                ProfileRepository profileRepository) {
        this.projectService = projectService;
        this.experienceService = experienceService;
        this.educationService = educationService;
        this.skillService = skillService;
        this.profileService = profileService;
        this.profileRepository = profileRepository;
    }
    public ProfileMasterResponse getProfileMasterData(String host) throws GenericException {
        Profile profile = profileRepository.findByWebsiteUrl(host);
        if(profile == null){
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST , "Profile Not Found");
        }
        ProfileResponse profileResponse = profileService.get(profile.getId()); // new service method to return ProfileResponse
        List<ProjectResponse> projects = projectService.getProjectByProfileId(profile.getId());
        List<ExperienceResponse> experiences = experienceService.getExperienceByProfileId(profile.getId());
        List<EducationResponse> educations = educationService.getEducationByProfileId(profile.getId());
        List<SkillResponse> skills = skillService.getSkillByProfileId(profile.getId());

        return ProfileMasterResponse.builder()
                .profile(profileResponse)
                .projects(projects)
                .experiences(experiences)
                .educations(educations)
                .skills(skills)
                .build();
    }
}