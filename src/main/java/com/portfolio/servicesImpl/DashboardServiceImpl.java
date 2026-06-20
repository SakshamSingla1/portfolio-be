package com.portfolio.servicesImpl;

import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.ActivityDTO;
import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileCompletionDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.StatsDTO;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;
import com.portfolio.entities.*;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.repositories.*;
import com.portfolio.services.DashboardService;
import com.portfolio.services.PortfolioViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final AchievementRepository achievementRepository;
    private final TestimonialRepository testimonialRepository;
    private final CertificationsRepository certificationsRepository;
    private final ContactUsRepository contactUsRepository;
    private final SocialLinkRepository socialLinkRepository;
    private final ProfileRepository profileRepository;
    private final PortfolioViewService portfolioViewService;

    @Override
    public DashboardSummaryDTO getDashboardSummary(String profileId) {

        Profile profile = profileRepository.findById(profileId).orElse(null);

        long totalSkills        = skillRepository.countByProfileId(profileId);
        long totalEducation     = educationRepository.countByProfileId(profileId);
        long totalExperience    = experienceRepository.countByProfileId(profileId);
        long totalProjects      = projectRepository.countByProfileId(profileId);
        long totalAchievements  = achievementRepository.countByProfileId(profileId);
        long totalTestimonials  = testimonialRepository.countByProfileId(profileId);
        long totalCertification = certificationsRepository.countByProfileId(profileId);
        long totalMessages      = contactUsRepository.countByProfileId(profileId);
        long unreadMessages     = contactUsRepository.countByStatusAndProfileId(ContactUsStatusEnum.UNREAD, profileId);
        long totalSocialLinks   = socialLinkRepository.countByProfileId(profileId);

        StatsDTO stats = StatsDTO.builder()
                .totalSkills(totalSkills)
                .totalEducation(totalEducation)
                .totalExperience(totalExperience)
                .totalProjects(totalProjects)
                .totalAchievements(totalAchievements)
                .totalTestimonials(totalTestimonials)
                .totalCertification(totalCertification)
                .totalMessages(totalMessages)
                .unreadMessages(unreadMessages)
                .totalSocialLinks(totalSocialLinks)
                .build();

        ProfileSummaryDTO profileSummary = buildProfileSummary(profile);
        ViewStatsDTO viewStats = portfolioViewService.getViewStats(profileId);

        ProfileCompletionDTO profileCompletion = calculateProfileCompletion(
                profile,
                totalProjects,
                totalSkills,
                totalExperience,
                totalTestimonials,
                totalEducation,
                totalCertification,
                totalAchievements,
                totalSocialLinks
        );

        List<ContactUsResponse> recentMessages = contactUsRepository
                .findTop5ByProfileIdOrderByCreatedAtDesc(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        List<ActivityDTO> recentActivities = buildLatestActivities(profileId);

        return DashboardSummaryDTO.builder()
                .profileSummary(profileSummary)
                .viewStats(viewStats)
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    // ---------------- PROFILE SUMMARY ----------------

    private ProfileSummaryDTO buildProfileSummary(Profile profile) {
        if (profile == null) return ProfileSummaryDTO.builder().build();
        return ProfileSummaryDTO.builder()
                .fullName(safe(profile.getFullName()))
                .title(safe(profile.getTitle()))
                .location(safe(profile.getLocation()))
                .profileImageUrl(safe(profile.getProfileImageUrl()))
                .build();
    }

    // ---------------- PROFILE COMPLETION ----------------

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

    // ---------------- RECENT ACTIVITY ----------------

    private List<ActivityDTO> buildLatestActivities(String profileId) {

        List<ActivityDTO> activities = new ArrayList<>();

        skillRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(skill -> activities.add(createActivity(
                        "SKILL",
                        buildAction("Skill", skill.getCreatedAt(), skill.getUpdatedAt(),
                                skill.getLogo() != null ? skill.getLogo().getName() : null),
                        skill.getUpdatedAt(),
                        skill.getId()
                )));

        educationRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(edu -> activities.add(createActivity(
                        "EDUCATION",
                        buildAction("Education", edu.getCreatedAt(), edu.getUpdatedAt(),
                                edu.getDegree() + " at " + edu.getInstitution()),
                        edu.getUpdatedAt(),
                        edu.getId()
                )));

        experienceRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(exp -> activities.add(createActivity(
                        "EXPERIENCE",
                        buildAction("Experience", exp.getCreatedAt(), exp.getUpdatedAt(),
                                exp.getJobTitle() + " at " + exp.getCompanyName()),
                        exp.getUpdatedAt(),
                        exp.getId()
                )));

        projectRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(project -> activities.add(createActivity(
                        "PROJECT",
                        buildAction("Project", project.getCreatedAt(), project.getUpdatedAt(),
                                project.getProjectName()),
                        project.getUpdatedAt(),
                        project.getId()
                )));

        achievementRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(achievement -> activities.add(createActivity(
                        "ACHIEVEMENT",
                        buildAction("Achievement", achievement.getCreatedAt(), achievement.getUpdatedAt(),
                                achievement.getTitle()),
                        achievement.getUpdatedAt(),
                        achievement.getId()
                )));

        certificationsRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(cert -> activities.add(createActivity(
                        "CERTIFICATION",
                        buildAction("Certification", cert.getCreatedAt(), cert.getUpdatedAt(),
                                cert.getTitle()),
                        cert.getUpdatedAt(),
                        cert.getId()
                )));

        testimonialRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(testimonial -> activities.add(createActivity(
                        "TESTIMONIAL",
                        "Testimonial received from " + safe(testimonial.getName()),
                        testimonial.getUpdatedAt(),
                        testimonial.getId()
                )));

        contactUsRepository.findTop1ByProfileIdOrderByCreatedAtDesc(profileId)
                .ifPresent(message -> activities.add(createActivity(
                        "MESSAGE",
                        "New message from " + safe(message.getName()),
                        message.getCreatedAt(),
                        message.getId()
                )));

        activities.sort(Comparator
                .comparing(ActivityDTO::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());

        return activities.stream().limit(10).toList();
    }

    private String buildAction(String entity, LocalDateTime created, LocalDateTime updated, String value) {
        String action = isCreated(created, updated) ? "added" : "updated";
        return entity + " " + action + ": " + safe(value);
    }

    private boolean isCreated(LocalDateTime created, LocalDateTime updated) {
        if (created == null || updated == null) return false;
        return created.isEqual(updated);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private ActivityDTO createActivity(String type, String description, LocalDateTime time, String entityId) {
        return ActivityDTO.builder()
                .type(type)
                .description(description)
                .timestamp(time)
                .entityId(entityId)
                .build();
    }

    private ContactUsResponse mapToResponse(ContactUs contact) {
        return ContactUsResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .status(contact.getStatus())
                .createdAt(contact.getCreatedAt())
                .build();
    }
}
