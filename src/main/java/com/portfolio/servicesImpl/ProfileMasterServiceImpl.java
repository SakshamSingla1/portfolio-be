package com.portfolio.servicesImpl;

import com.portfolio.dtos.*;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileMasterServiceImpl implements ProfileMasterService{

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final SkillService skillService;
    private final ProfileRepository profileRepository;

    @Override
    public ProfileMasterResponse getProfileMasterData(String host) throws GenericException {
        Profile profile = profileRepository.findByUserName(host)
                .orElseThrow(() ->
                        new GenericException(
                                ExceptionCodeEnum.BAD_REQUEST,
                                "Profile not found for host: " + host
                        ));

        String profileId = profile.getId();

        return ProfileMasterResponse.builder()
                .profile(profileService.get(profileId))
                .projects(projectService.getByProfile(profileId))
                .experiences(experienceService.getByProfile(profileId))
                .educations(educationService.getByProfile(profileId))
                .skills(skillService.getByProfile(profileId))
                .build();
    }
}
