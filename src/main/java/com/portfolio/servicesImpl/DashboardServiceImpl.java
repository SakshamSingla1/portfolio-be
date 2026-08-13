package com.portfolio.servicesImpl;

import com.portfolio.dao.contact_us.ContactUsDao;
import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.profile_completion.ProfileCompletionSnapshotDao;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.ActivityDTO;
import com.portfolio.dtos.DashboardDTOs.CompletionSnapshotDTO;
import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileCompletionDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.StatsDTO;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Profile;
import com.portfolio.entities.ProfileCompletionSnapshot;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.services.DashboardService;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.services.SocialLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContactUsDao contactUsDao;
    private final ProfileDao profileDao;
    private final PortfolioViewService portfolioViewService;
    private final FileAssetDao fileAssetDao;
    private final SocialLinkService socialLinkService;
    private final ProfileCompletionSnapshotDao profileCompletionSnapshotDao;
    private final ExecutorService profileAggregationExecutor;

    @Override
    public DashboardSummaryDTO getDashboardSummary(Long profileId) {
        // All 6 data fetches are independent — run them in parallel, bounded by the
        // same pool ProfileMasterServiceImpl uses so this doesn't oversubscribe the
        // HikariCP pool alongside the common ForkJoinPool's own unbounded fan-out.
        CompletableFuture<Profile> profileFuture = CompletableFuture.supplyAsync(
                () -> profileDao.findById(profileId).orElse(null), profileAggregationExecutor);
        CompletableFuture<StatsDTO> statsFuture = CompletableFuture.supplyAsync(
                () -> profileDao.getDashboardStats(profileId), profileAggregationExecutor);
        CompletableFuture<Map<String, Long>> statsDeltaFuture = CompletableFuture.supplyAsync(
                () -> profileDao.getDashboardStatsDeltas(profileId), profileAggregationExecutor);
        CompletableFuture<ViewStatsDTO> viewStatsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewService.getViewStats(profileId), profileAggregationExecutor);
        CompletableFuture<List<ContactUsResponse>> messagesFuture = CompletableFuture.supplyAsync(
                () -> contactUsDao.findTop5DTOByProfileIdOrderByCreatedAtDesc(profileId), profileAggregationExecutor);
        CompletableFuture<List<ActivityDTO>> activitiesFuture = CompletableFuture.supplyAsync(
                () -> profileDao.getLatestActivities(profileId), profileAggregationExecutor);
        CompletableFuture<List<FileAsset>> assetsFuture = CompletableFuture.supplyAsync(
                () -> fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profileId, ResourceTypeEnum.PROFILE), profileAggregationExecutor);
        CompletableFuture<List<SocialLinkResponseDTO>> socialLinksFuture = CompletableFuture.supplyAsync(
                () -> socialLinkService.getByProfile(profileId), profileAggregationExecutor);

        CompletableFuture.allOf(profileFuture, statsFuture, statsDeltaFuture, viewStatsFuture, messagesFuture, activitiesFuture, assetsFuture, socialLinksFuture).join();

        Profile profile             = profileFuture.join();
        StatsDTO stats              = statsFuture.join();
        Map<String, Long> statsDelta = statsDeltaFuture.join();
        ViewStatsDTO viewStats      = viewStatsFuture.join();
        List<ContactUsResponse> recentMessages  = messagesFuture.join();
        List<ActivityDTO> recentActivities      = activitiesFuture.join();
        List<FileAsset> assets      = assetsFuture.join();
        List<SocialLinkResponseDTO> socialLinks = socialLinksFuture.join();

        stats.setWeeklyDelta(statsDelta);

        ProfileSummaryDTO profileSummary = buildProfileSummary(profile, assets, socialLinks);
        ProfileCompletionDTO profileCompletion = calculateProfileCompletion(
                profile,
                stats.getTotalProjects(),
                stats.getTotalSkills(),
                stats.getTotalExperience(),
                stats.getTotalTestimonials(),
                stats.getTotalEducation(),
                stats.getTotalCertification(),
                stats.getTotalAchievements(),
                stats.getTotalSocialLinks()
        );

        profileCompletionSnapshotDao.upsertSnapshot(profileId, LocalDate.now(), profileCompletion.getPercentage());
        List<ProfileCompletionSnapshot> snapshots = profileCompletionSnapshotDao.findSince(profileId, LocalDate.now().minusDays(29));
        profileCompletion.setTrend(buildCompletionTrend(snapshots));

        return DashboardSummaryDTO.builder()
                .profileSummary(profileSummary)
                .viewStats(viewStats)
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    private ProfileSummaryDTO buildProfileSummary(Profile profile, List<FileAsset> profileAssets, List<SocialLinkResponseDTO> socialLinks) {
        if (profile == null) return ProfileSummaryDTO.builder().build();
        String profileImageUrl = null;
        for (FileAsset asset : profileAssets) {
            if (asset.isPrimary() || "PROFILE_IMAGE".equals(asset.getMetaData())) {
                profileImageUrl = asset.getPath();
                break;
            }
        }
        return ProfileSummaryDTO.builder()
                .fullName(safe(profile.getFullName()))
                .title(safe(profile.getTitle()))
                .location(safe(profile.getLocation()))
                .profileImageUrl(safe(profileImageUrl))
                .portfolioUrl(resolvePortfolioUrl(socialLinks))
                .build();
    }

    // Mirrors EmbedController's resolution order: an explicit "Website" link
    // wins (most users treat this as their real public URL), falling back to
    // the "Portfolio" platform link if no separate website is set.
    private String resolvePortfolioUrl(List<SocialLinkResponseDTO> socialLinks) {
        String websiteUrl = findActiveSocialUrl(socialLinks, PlatformEnum.WEBSITE);
        if (websiteUrl != null) return websiteUrl;
        return findActiveSocialUrl(socialLinks, PlatformEnum.PORTFOLIO);
    }

    private String findActiveSocialUrl(List<SocialLinkResponseDTO> socialLinks, PlatformEnum platform) {
        if (socialLinks == null) return null;
        return socialLinks.stream()
                .filter(l -> platform.equals(l.getPlatform()) && StatusEnum.ACTIVE.equals(l.getStatus()))
                .map(SocialLinkResponseDTO::getUrl)
                .findFirst()
                .orElse(null);
    }

    private ProfileCompletionDTO calculateProfileCompletion(
            Profile profile,
            long projects,
            long skills,
            long experience,
            long testimonials,
            long education,
            long certifications,
            long achievements,
            long socialLinks
    ) {
        int score = 0;
        List<String> missingSections = new ArrayList<>();

        boolean hasProfileBasics = profile != null
                && profile.getTitle() != null && !profile.getTitle().isBlank()
                && profile.getLocation() != null && !profile.getLocation().isBlank();
        score += addScore(hasProfileBasics,      10, "Complete your profile (title & location)", missingSections);
        score += addScore(projects > 0,          15, "Add at least one project",                 missingSections);
        score += addScore(skills >= 5,           15, "Add at least 5 skills",                   missingSections);
        score += addScore(experience > 0,        15, "Add work experience",                      missingSections);
        score += addScore(education > 0,         10, "Add education",                            missingSections);
        score += addScore(testimonials > 0,      10, "Add testimonials",                         missingSections);
        score += addScore(certifications > 0,    10, "Add certifications",                       missingSections);
        score += addScore(achievements > 0,       5, "Add achievements",                         missingSections);
        score += addScore(socialLinks > 0,       10, "Add social links",                         missingSections);
        return ProfileCompletionDTO.builder()
                .percentage(Math.min(score, 100))
                .missingSections(missingSections)
                .build();
    }

    private List<CompletionSnapshotDTO> buildCompletionTrend(List<ProfileCompletionSnapshot> snapshots) {
        List<CompletionSnapshotDTO> trend = new ArrayList<>();
        for (ProfileCompletionSnapshot s : snapshots) {
            LocalDate date = s.getSnapshotDate();
            String dateStr = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.getDayOfMonth();
            trend.add(CompletionSnapshotDTO.builder().date(dateStr).percentage(s.getPercentage()).build());
        }
        return trend;
    }

    private int addScore(boolean condition, int value, String message, List<String> missing) {
        if (condition) return value;
        missing.add(message);
        return 0;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
