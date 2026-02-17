package com.portfolio.servicesImpl;

import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.ActivityDTO;
import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.dtos.DashboardDTOs.ProfileCompletionDTO;
import com.portfolio.dtos.DashboardDTOs.StatsDTO;
import com.portfolio.entities.*;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.repositories.*;
import com.portfolio.services.DashboardService;
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

    @Override
    public DashboardSummaryDTO getDashboardSummary(String profileId) {

        // ✅ FAST COUNTS (indexed queries, no async overhead)
        long totalSkills = skillRepository.countByProfileId(profileId);
        long totalEducation = educationRepository.countByProfileId(profileId);
        long totalExperience = experienceRepository.countByProfileId(profileId);
        long totalProjects = projectRepository.countByProfileId(profileId);
        long totalAchievements = achievementRepository.countByProfileId(profileId);
        long totalTestimonials = testimonialRepository.countByProfileId(profileId);
        long totalCertification = certificationsRepository.countByProfileId(profileId);
        long totalMessages = contactUsRepository.countByProfileId(profileId);
        long unreadMessages = contactUsRepository.countByStatusAndProfileId(
                ContactUsStatusEnum.UNREAD,
                profileId
        );

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
                .build();

        ProfileCompletionDTO profileCompletion =
                calculateProfileCompletion(
                        totalProjects,
                        totalSkills,
                        totalExperience,
                        totalTestimonials,
                        totalEducation,
                        totalCertification
                );

        // ✅ Uses indexed compound index (profileId + createdAt)
        List<ContactUsResponse> recentMessages =
                contactUsRepository
                        .findTop5ByProfileIdOrderByCreatedAtDesc(profileId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        // ✅ Optimized activity building (still readable, indexed)
        List<ActivityDTO> recentActivities = buildLatestActivities(profileId);

        return DashboardSummaryDTO.builder()
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    // ---------------- PROFILE COMPLETION ----------------

    private ProfileCompletionDTO calculateProfileCompletion(
            long projects,
            long skills,
            long experience,
            long testimonials,
            long education,
            long certifications
    ) {

        int score = 0;
        List<String> missingSections = new ArrayList<>();

        score += addScore(projects > 0, 20, "Add at least one project", missingSections);
        score += addScore(skills >= 5, 15, "Add at least 5 skills", missingSections);
        score += addScore(experience > 0, 20, "Add work experience", missingSections);
        score += addScore(education > 0, 15, "Add education", missingSections);
        score += addScore(testimonials > 0, 10, "Add testimonials", missingSections);
        score += addScore(certifications > 0, 10, "Add certifications", missingSections);

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
                .ifPresent(skill ->
                        activities.add(
                                createActivity(
                                        "SKILL",
                                        buildAction("Skill",
                                                skill.getCreatedAt(),
                                                skill.getUpdatedAt(),
                                                safe(skill.getLogo() != null ? skill.getLogo().getName() : null)
                                        ),
                                        skill.getUpdatedAt()
                                )
                        )
                );

        educationRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(edu ->
                        activities.add(
                                createActivity(
                                        "EDUCATION",
                                        buildAction(
                                                "Education",
                                                edu.getCreatedAt(),
                                                edu.getUpdatedAt(),
                                                edu.getDegree() + " at " + edu.getInstitution()
                                        ),
                                        edu.getUpdatedAt()
                                )
                        )
                );

        experienceRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(exp ->
                        activities.add(
                                createActivity(
                                        "EXPERIENCE",
                                        buildAction(
                                                "Experience",
                                                exp.getCreatedAt(),
                                                exp.getUpdatedAt(),
                                                exp.getJobTitle() + " at " + exp.getCompanyName()
                                        ),
                                        exp.getUpdatedAt()
                                )
                        )
                );

        projectRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(project ->
                        activities.add(
                                createActivity(
                                        "PROJECT",
                                        buildAction(
                                                "Project",
                                                project.getCreatedAt(),
                                                project.getUpdatedAt(),
                                                project.getProjectName()
                                        ),
                                        project.getUpdatedAt()
                                )
                        )
                );

        achievementRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(achievement ->
                        activities.add(
                                createActivity(
                                        "ACHIEVEMENT",
                                        buildAction(
                                                "Achievement",
                                                achievement.getCreatedAt(),
                                                achievement.getUpdatedAt(),
                                                achievement.getTitle()
                                        ),
                                        achievement.getUpdatedAt()
                                )
                        )
                );

        certificationsRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(cert ->
                        activities.add(
                                createActivity(
                                        "CERTIFICATION",
                                        buildAction(
                                                "Certification",
                                                cert.getCreatedAt(),
                                                cert.getUpdatedAt(),
                                                cert.getTitle()
                                        ),
                                        cert.getUpdatedAt()
                                )
                        )
                );

        testimonialRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId)
                .ifPresent(testimonial ->
                        activities.add(
                                createActivity(
                                        "TESTIMONIAL",
                                        "Testimonial received from " + safe(testimonial.getName()),
                                        testimonial.getUpdatedAt()
                                )
                        )
                );

        contactUsRepository.findTop1ByProfileIdOrderByCreatedAtDesc(profileId)
                .ifPresent(message ->
                        activities.add(
                                createActivity(
                                        "MESSAGE",
                                        "New message from " + safe(message.getName()),
                                        message.getCreatedAt()
                                )
                        )
                );

        activities.sort(
                Comparator.comparing(
                        ActivityDTO::getTimestamp,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );

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

    private ActivityDTO createActivity(String type, String description, LocalDateTime time) {
        return ActivityDTO.builder()
                .type(type)
                .description(description)
                .timestamp(time)
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
