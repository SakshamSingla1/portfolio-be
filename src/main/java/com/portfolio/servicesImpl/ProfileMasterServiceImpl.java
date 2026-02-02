package com.portfolio.servicesImpl;

import com.portfolio.dtos.*;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.SocialLinkRepository;
import com.portfolio.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileMasterServiceImpl implements ProfileMasterService {

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final SkillService skillService;
    private final AchievementService achievementService;
    private final TestimonialService testimonialService;
    private final CertificationService certificationService;
    private final SocialLinkService socialLinkService;
    private final SocialLinkRepository socialLinkRepository;
    private final ColorThemeService colorThemeService;

    @Override
    public ProfileMasterResponse getProfileMasterData(String host)
            throws GenericException {
        String domain = normalizeDomain(host);
        SocialLinks socialLink = socialLinkRepository.findByPlatformAndUrl(PlatformEnum.PORTFOLIO, domain)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND,"Social Link not found"));
        String profileId = socialLink.getProfileId();
        ProfileResponse profileResponse = profileService.get(profileId);
        return ProfileMasterResponse.builder()
                .profile(profileResponse)
                .colorTheme(colorThemeService.getThemeByName(profileResponse.getThemeName()))
                .projects(projectService.getByProfile(profileId))
                .experiences(experienceService.getByProfile(profileId))
                .educations(educationService.getByProfile(profileId))
                .skills(skillService.getByProfile(profileId))
                .achievements(achievementService.getByProfile(profileId))
                .testimonials(testimonialService.getByProfile(profileId))
                .certifications(certificationService.getByProfile(profileId))
                .socialLinks(socialLinkService.getByProfile(profileId))
                .build();
    }

    private String normalizeDomain(String host) throws GenericException {
        if (host == null || host.isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Invalid domain");
        }
        return host
                .toLowerCase()
                .replace("www.", "")
                .trim();
    }
}
