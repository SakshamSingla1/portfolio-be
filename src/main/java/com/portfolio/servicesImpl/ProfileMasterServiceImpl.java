package com.portfolio.servicesImpl;

import com.portfolio.dao.seo_meta.SeoMetaDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.dtos.GitHub.GithubRepoResponse;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.entities.SeoMeta;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.*;
import com.portfolio.services.GithubIntegrationService;
import com.portfolio.services.ProfileLanguageService;
import com.portfolio.services.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

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
    private final GithubIntegrationService githubIntegrationService;
    private final SeoMetaDao seoMetaDao;
    private final ProfileLanguageService profileLanguageService;
    private final ServiceOfferingService serviceOfferingService;
    private final com.portfolio.services.PublicationService publicationService;
    private final ExecutorService profileAggregationExecutor;

    @Override
    public ProfileMasterResponse getProfileMasterData(String host)
            throws GenericException {
        String domain = normalizeDomain(host);
        SocialLinks socialLink = socialLinksDao.findByPlatformAndUrl(PlatformEnum.PORTFOLIO, domain)
                .orElseThrow(
                        () -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social Link not found"));
        return buildResponse(socialLink.getProfileId(), true);
    }

    @Override
    public ProfileMasterResponse getByProfileId(Long profileId) throws GenericException {
        return buildResponse(profileId, true);
    }

    @Override
    public ProfileMasterResponse getForResumeExport(Long profileId) throws GenericException {
        return buildResponse(profileId, false);
    }

    // All of these are independent per-profileId lookups (no dependency on each other except
    // colorTheme, which needs the resolved theme mapping first) — fetched concurrently instead
    // of sequentially, since each round-trip carries real network latency to the DB and doing
    // ~13 of them one after another was the dominant cost of building this response (observed
    // ~400-500ms per call, ~6-7s total sequentially for the resume PDF export alone).
    private ProfileMasterResponse buildResponse(Long profileId, boolean includeGithubAndSeo) throws GenericException {
        CompletableFuture<ProfileResponse> profileF = supplyAsync(() -> profileService.get(profileId));
        CompletableFuture<ProfileThemeResponse> themeF = supplyAsync(() -> profileThemeService.getThemeByProfileId(profileId));
        CompletableFuture<List<SocialLinkResponseDTO>> socialLinksF = supplyAsync(() -> socialLinkService.getByProfile(profileId));
        CompletableFuture<?> projectsF = supplyAsync(() -> projectService.getByProfile(profileId));
        CompletableFuture<?> experiencesF = supplyAsync(() -> experienceService.getByProfile(profileId));
        CompletableFuture<?> educationsF = supplyAsync(() -> educationService.getByProfile(profileId));
        CompletableFuture<?> skillsF = supplyAsync(() -> skillService.getByProfile(profileId));
        CompletableFuture<?> achievementsF = supplyAsync(() -> achievementService.getByProfile(profileId));
        CompletableFuture<?> certificationsF = supplyAsync(() -> certificationService.getByProfile(profileId));
        CompletableFuture<?> languagesF = supplyAsync(() -> profileLanguageService.getByProfile(profileId));
        CompletableFuture<?> servicesF = supplyAsync(() -> serviceOfferingService.getByProfile(profileId));
        CompletableFuture<?> publicationsF = supplyAsync(() -> publicationService.getByProfile(profileId));
        CompletableFuture<ColorThemeResponseDTO> colorThemeF = themeF.thenComposeAsync(
                theme -> supplyAsync(() -> colorThemeService.getThemeById(theme.getThemeId())),
                profileAggregationExecutor);

        CompletableFuture<GitHubStatsDTO> githubStatsF = null;
        CompletableFuture<List<GithubRepoResponse>> githubReposF = null;
        CompletableFuture<SeoMetaResponseDTO> seoMetaF = null;
        CompletableFuture<List<TestimonialResponseDTO>> testimonialsF = null;
        if (includeGithubAndSeo) {
            githubStatsF = socialLinksF.thenApplyAsync(links -> resolveGithubStats(profileId, links), profileAggregationExecutor);
            githubReposF = supplyAsync(() -> githubIntegrationService.getVisibleRepos(profileId));
            seoMetaF = supplyAsync(() -> seoMetaDao.findByProfileIdAndPageKey(profileId, PageKeyEnum.HOME)
                    .map(this::toSeoDto)
                    .orElse(null));
            testimonialsF = supplyAsync(() -> testimonialService.getByProfile(profileId));
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(
                profileF, themeF, socialLinksF, projectsF, experiencesF, educationsF, skillsF,
                achievementsF, certificationsF, languagesF, servicesF, publicationsF, colorThemeF);
        if (includeGithubAndSeo) {
            all = CompletableFuture.allOf(all, githubStatsF, githubReposF, seoMetaF, testimonialsF);
        }
        join(all);

        ProfileMasterResponse.ProfileMasterResponseBuilder builder = ProfileMasterResponse.builder()
                .profile(join(profileF))
                .colorTheme(join(colorThemeF))
                .projects(join(cast(projectsF)))
                .experiences(join(cast(experiencesF)))
                .educations(join(cast(educationsF)))
                .skills(join(cast(skillsF)))
                .achievements(join(cast(achievementsF)))
                .certifications(join(cast(certificationsF)))
                .socialLinks(join(socialLinksF))
                .languages(join(cast(languagesF)))
                .services(join(cast(servicesF)))
                .publications(join(cast(publicationsF)));

        if (includeGithubAndSeo) {
            builder.testimonials(join(testimonialsF))
                    .githubStats(join(githubStatsF))
                    .githubRepos(join(githubReposF))
                    .seoMeta(join(seoMetaF));
        }

        return builder.build();
    }

    // Prefer cached stats from OAuth integration; fall back to live public-API fetch
    private GitHubStatsDTO resolveGithubStats(Long profileId, List<SocialLinkResponseDTO> socialLinks) {
        return githubIntegrationService.getCachedStats(profileId)
                .orElseGet(() -> socialLinks.stream()
                        .filter(l -> PlatformEnum.GITHUB.equals(l.getPlatform())
                                && StatusEnum.ACTIVE.equals(l.getStatus()))
                        .findFirst()
                        .map(l -> gitHubService.fetchStats(l.getUrl()))
                        .orElse(null));
    }

    @SuppressWarnings("unchecked")
    private <T> CompletableFuture<T> cast(CompletableFuture<?> future) {
        return (CompletableFuture<T>) future;
    }

    @FunctionalInterface
    private interface CheckedSupplier<T> {
        T get() throws GenericException;
    }

    private <T> CompletableFuture<T> supplyAsync(CheckedSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (GenericException e) {
                throw new CompletionException(e);
            }
        }, profileAggregationExecutor);
    }

    private <T> T join(CompletableFuture<T> future) throws GenericException {
        try {
            return future.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            while (cause instanceof CompletionException) {
                cause = cause.getCause();
            }
            if (cause instanceof GenericException) {
                throw (GenericException) cause;
            }
            throw e;
        }
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
                .indexable(e.isIndexable())
                .followLinks(e.isFollowLinks())
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
