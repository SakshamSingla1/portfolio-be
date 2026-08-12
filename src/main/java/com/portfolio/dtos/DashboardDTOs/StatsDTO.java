package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StatsDTO {
    private long totalSkills;
    private long totalEducation;
    private long totalExperience;
    private long totalProjects;
    private long totalAchievements;
    private long totalTestimonials;
    private long totalCertification;
    private long totalMessages;
    private long unreadMessages;
    private long totalSocialLinks;
    // Keyed by the same names as the totals above (totalSkills, totalExperience,
    // totalProjects, totalMessages) — count created this week minus count
    // created the week before, so the dashboard can show "+3 this week".
    private Map<String, Long> weeklyDelta;
}
