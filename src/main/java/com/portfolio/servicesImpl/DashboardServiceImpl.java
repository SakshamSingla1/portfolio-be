package com.portfolio.servicesImpl;

import com.portfolio.dao.contact_us.ContactUsDao;
import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.ActivityDTO;
import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileCompletionDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.StatsDTO;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.services.DashboardService;
import com.portfolio.services.PortfolioViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContactUsDao contactUsDao;
    private final ProfileDao profileDao;
    private final PortfolioViewService portfolioViewService;
    private final FileAssetDao fileAssetDao;

    @Override
    public DashboardSummaryDTO getDashboardSummary(Long profileId) {
        // All 6 data fetches are independent — run them in parallel
        CompletableFuture<Profile> profileFuture = CompletableFuture.supplyAsync(
                () -> profileDao.findById(profileId).orElse(null));
        CompletableFuture<StatsDTO> statsFuture = CompletableFuture.supplyAsync(
                () -> profileDao.getDashboardStats(profileId));
        CompletableFuture<ViewStatsDTO> viewStatsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewService.getViewStats(profileId));
        CompletableFuture<List<ContactUsResponse>> messagesFuture = CompletableFuture.supplyAsync(
                () -> contactUsDao.findTop5DTOByProfileIdOrderByCreatedAtDesc(profileId));
        CompletableFuture<List<ActivityDTO>> activitiesFuture = CompletableFuture.supplyAsync(
                () -> profileDao.getLatestActivities(profileId));
        CompletableFuture<List<FileAsset>> assetsFuture = CompletableFuture.supplyAsync(
                () -> fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profileId, ResourceTypeEnum.PROFILE));

        CompletableFuture.allOf(profileFuture, statsFuture, viewStatsFuture, messagesFuture, activitiesFuture, assetsFuture).join();

        Profile profile             = profileFuture.join();
        StatsDTO stats              = statsFuture.join();
        ViewStatsDTO viewStats      = viewStatsFuture.join();
        List<ContactUsResponse> recentMessages  = messagesFuture.join();
        List<ActivityDTO> recentActivities      = activitiesFuture.join();
        List<FileAsset> assets      = assetsFuture.join();

        ProfileSummaryDTO profileSummary = buildProfileSummary(profile, assets);
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

        return DashboardSummaryDTO.builder()
                .profileSummary(profileSummary)
                .viewStats(viewStats)
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    private ProfileSummaryDTO buildProfileSummary(Profile profile, List<FileAsset> profileAssets) {
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
                .build();
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

    private int addScore(boolean condition, int value, String message, List<String> missing) {
        if (condition) return value;
        missing.add(message);
        return 0;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
