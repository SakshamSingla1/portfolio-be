package com.portfolio.dtos;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileMasterResponse {
    private ProfileResponse profile;
    private ColorThemeResponseDTO colorTheme;
    private List<ProjectResponse> projects;
    private List<ExperienceResponse> experiences;
    private List<EducationResponse> educations;
    private List<SkillResponse> skills;
    private List<AchievementResponseDTO> achievements;
    private List<TestimonialResponseDTO> testimonials;
    private List<CertificationResponseDTO> certifications;
    private List<SocialLinkResponseDTO> socialLinks;
}
