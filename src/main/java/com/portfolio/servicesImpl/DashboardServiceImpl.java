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

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContactUsDao contactUsDao;
    private final ProfileDao profileDao;
    private final PortfolioViewService portfolioViewService;
    private final FileAssetDao fileAssetDao;

    @Override
    public DashboardSummaryDTO getDashboardSummary(Long profileId) {

        Profile profile = profileDao.findById(profileId).orElse(null);

        StatsDTO stats = profileDao.getDashboardStats(profileId);

        ProfileSummaryDTO profileSummary = buildProfileSummary(profile);
        ViewStatsDTO viewStats = portfolioViewService.getViewStats(profileId);

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

        List<ContactUsResponse> recentMessages = contactUsDao.findTop5DTOByProfileIdOrderByCreatedAtDesc(profileId);
        List<ActivityDTO> recentActivities = profileDao.getLatestActivities(profileId);

        return DashboardSummaryDTO.builder()
                .profileSummary(profileSummary)
                .viewStats(viewStats)
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    private ProfileSummaryDTO buildProfileSummary(Profile profile) {
        if (profile == null) return ProfileSummaryDTO.builder().build();
        String profileImageUrl = null;
        List<FileAsset> profileAssets = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profile.getId().intValue(), ResourceTypeEnum.PROFILE);
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
