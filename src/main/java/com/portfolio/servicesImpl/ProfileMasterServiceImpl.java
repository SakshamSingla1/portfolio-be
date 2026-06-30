package com.portfolio.servicesImpl;

import com.portfolio.dao.seo_meta.SeoMetaDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.SeoMeta;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final SocialLinksDao socialLinksDao;
    private final ColorThemeService colorThemeService;
    private final ProfileThemeService profileThemeService;
    private final GitHubService gitHubService;
    private final SeoMetaDao seoMetaDao;

    @Override
    public ProfileMasterResponse getProfileMasterData(String host)
            throws GenericException {
        String domain = normalizeDomain(host);
        SocialLinks socialLink = socialLinksDao.findByPlatformAndUrl(PlatformEnum.PORTFOLIO, domain)
                .orElseThrow(
                        () -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social Link not found"));
        Long profileId = socialLink.getProfileId();
        ProfileResponse profileResponse = profileService.get(profileId);
        ProfileThemeResponse theme = profileThemeService.getThemeByProfileId(profileId);
        List<SocialLinkResponseDTO> socialLinks = socialLinkService.getByProfile(profileId);

        GitHubStatsDTO githubStats = socialLinks.stream()
                .filter(l -> PlatformEnum.GITHUB.equals(l.getPlatform())
                        && StatusEnum.ACTIVE.equals(l.getStatus()))
                .findFirst()
                .map(l -> gitHubService.fetchStats(l.getUrl()))
                .orElse(null);

        SeoMetaResponseDTO seoMeta = seoMetaDao
                .findByProfileIdAndPageKey(profileId, PageKeyEnum.HOME)
                .map(this::toSeoDto)
                .orElse(null);

        return ProfileMasterResponse.builder()
                .profile(profileResponse)
                .colorTheme(colorThemeService.getThemeById(theme.getThemeId()))
                .projects(projectService.getByProfile(profileId))
                .experiences(experienceService.getByProfile(profileId))
                .educations(educationService.getByProfile(profileId))
                .skills(skillService.getByProfile(profileId))
                .achievements(achievementService.getByProfile(profileId))
                .testimonials(testimonialService.getByProfile(profileId))
                .certifications(certificationService.getByProfile(profileId))
                .socialLinks(socialLinks)
                .githubStats(githubStats)
                .seoMeta(seoMeta)
                .build();
    }

    private SeoMetaResponseDTO toSeoDto(SeoMeta e) {
        return SeoMetaResponseDTO.builder()
                .id(e.getId())
                .pageKey(e.getPageKey())
                .title(e.getTitle())
                .description(e.getDescription())
                .keywords(e.getKeywords())
                .ogTitle(e.getOgTitle())
                .ogDescription(e.getOgDescription())
                .ogImageUrl(e.getOgImageUrl())
                .canonicalUrl(e.getCanonicalUrl())
                .indexable(e.getIndexable())
                .followLinks(e.getFollowLinks())
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
