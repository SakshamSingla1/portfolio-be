package com.portfolio.dtos.DashboardDTOs;

import lombok.Builder;
import lombok.Data;

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
}
