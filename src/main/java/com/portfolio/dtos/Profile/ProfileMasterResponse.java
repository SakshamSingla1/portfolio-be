package com.portfolio.dtos.Profile;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.dtos.GitHub.GithubRepoResponse;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
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
    private GitHubStatsDTO githubStats;
    private List<GithubRepoResponse> githubRepos;
    private SeoMetaResponseDTO seoMeta;
    private List<ProfileLanguageResponse> languages;
    private List<ServiceResponse> services;
}
