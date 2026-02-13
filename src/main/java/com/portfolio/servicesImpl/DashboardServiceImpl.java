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

        // =====================================================
        // 1️⃣ Stats Section
        // =====================================================

        long totalSkills = skillRepository.countByProfileId(profileId);
        long totalEducation = educationRepository.countByProfileId(profileId);
        long totalExperience = experienceRepository.countByProfileId(profileId);
        long totalProjects = projectRepository.countByProfileId(profileId);
        long totalAchievements = achievementRepository.countByProfileId(profileId);
        long totalTestimonials = testimonialRepository.countByProfileId(profileId);
        long totalCertification = certificationsRepository.countByProfileId(profileId);
        long totalMessages = contactUsRepository.countByProfileId(profileId);

        long unreadMessages =
                contactUsRepository.countByStatusAndProfileId(
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

        // =====================================================
        // 2️⃣ Profile Completion Section
        // =====================================================

        ProfileCompletionDTO profileCompletion =
                calculateProfileCompletion(
                        totalProjects,
                        totalSkills,
                        totalExperience,
                        totalTestimonials,
                        totalEducation,
                        totalCertification
                );

        // =====================================================
        // 3️⃣ Recent Messages
        // =====================================================

        List<ContactUsResponse> recentMessages =
                contactUsRepository
                        .findTop5ByProfileIdOrderByCreatedAtDesc(profileId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        // =====================================================
        // 4️⃣ Activity Feed (Fetch → Then Map)
        // =====================================================

        List<ActivityDTO> recentActivities =
                buildLatestActivities(profileId);

        return DashboardSummaryDTO.builder()
                .stats(stats)
                .profileCompletion(profileCompletion)
                .recentMessages(recentMessages)
                .recentActivities(recentActivities)
                .build();
    }

    // =====================================================
    // Weighted Profile Completion
    // =====================================================

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

        if (projects > 0) score += 20;
        else missingSections.add("Add at least one project");

        if (skills >= 5) score += 15;
        else missingSections.add("Add at least 5 skills");

        if (experience > 0) score += 20;
        else missingSections.add("Add work experience");

        if (education > 0) score += 15;
        else missingSections.add("Add education");

        if (testimonials > 0) score += 10;
        else missingSections.add("Add testimonials");

        if (certifications > 0) score += 10;
        else missingSections.add("Add certifications");

        return ProfileCompletionDTO.builder()
                .percentage(Math.min(score, 100))
                .missingSections(missingSections)
                .build();
    }

    // =====================================================
    // Fetch Latest Record First → Then Map to Activity
    // =====================================================

    private List<ActivityDTO> buildLatestActivities(String profileId) {

        List<ActivityDTO> activities = new ArrayList<>();

        // 1️⃣ Skill
        Optional<Skill> latestSkill =
                skillRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestSkill.ifPresent(skill ->
                activities.add(
                        createActivity(
                                "SKILL",
                                "Skill updated: " + skill.getLogo().getName(),
                                skill.getUpdatedAt()
                        )
                )
        );

        // 2️⃣ Education
        Optional<Education> latestEducation =
                educationRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestEducation.ifPresent(education ->
                activities.add(
                        createActivity(
                                "EDUCATION",
                                "Education updated: " + education.getDegree(),
                                education.getUpdatedAt()
                        )
                )
        );

        // 3️⃣ Experience
        Optional<Experience> latestExperience =
                experienceRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestExperience.ifPresent(exp ->
                activities.add(
                        createActivity(
                                "EXPERIENCE",
                                "Experience updated",
                                exp.getUpdatedAt()
                        )
                )
        );

        // 4️⃣ Project
        Optional<Project> latestProject =
                projectRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestProject.ifPresent(project ->
                activities.add(
                        createActivity(
                                "PROJECT",
                                "Project updated: " + project.getProjectName(),
                                project.getUpdatedAt()
                        )
                )
        );

        // 5️⃣ Achievement
        Optional<Achievements> latestAchievement =
                achievementRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestAchievement.ifPresent(achievement ->
                activities.add(
                        createActivity(
                                "ACHIEVEMENT",
                                "Achievement updated",
                                achievement.getUpdatedAt()
                        )
                )
        );

        // 6️⃣ Certification
        Optional<Certifications> latestCertification =
                certificationsRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestCertification.ifPresent(cert ->
                activities.add(
                        createActivity(
                                "CERTIFICATION",
                                "Certification updated",
                                cert.getUpdatedAt()
                        )
                )
        );

        // 7️⃣ Testimonial
        Optional<Testimonial> latestTestimonial =
                testimonialRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);

        latestTestimonial.ifPresent(testimonial ->
                activities.add(
                        createActivity(
                                "TESTIMONIAL",
                                "Testimonial updated",
                                testimonial.getUpdatedAt()
                        )
                )
        );

        // 8️⃣ Message
        Optional<ContactUs> latestMessage =
                contactUsRepository.findTop1ByProfileIdOrderByCreatedAtDesc(profileId);

        latestMessage.ifPresent(message ->
                activities.add(
                        createActivity(
                                "MESSAGE",
                                "Message updated from " + message.getName(),
                                message.getCreatedAt()
                        )
                )
        );

        // Sort by updatedAt DESC
        activities.sort(
                Comparator.comparing(
                        ActivityDTO::getTimestamp,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );

        return activities.stream().limit(10).toList();
    }

    private ActivityDTO createActivity(String type, String description, java.time.LocalDateTime time) {
        return ActivityDTO.builder()
                .type(type)
                .description(description)
                .timestamp(time)
                .build();
    }

    // =====================================================
    // Map ContactUs → Response
    // =====================================================

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
