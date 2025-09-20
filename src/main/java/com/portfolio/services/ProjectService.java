package com.portfolio.services;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Project;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.ProjectRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    public ProjectService(ProjectRepository projectRepository, SkillRepository skillRepository, ProfileRepository profileRepository) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.profileRepository = profileRepository;
    }

    // ---------------- CREATE PROJECT ----------------
    public ProjectResponse createProject(ProjectRequest request) throws GenericException {
        if (projectRepository.existsByProjectNameAndProfileId(request.getProjectName(), request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Project with same name exists");
        }

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        List<Skill> skills = skillRepository.findAllById(request.getTechnologiesUsed());

        Project project = Project.builder()
                .projectName(request.getProjectName())
                .projectDescription(request.getProjectDescription())
                .projectLink(request.getProjectLink())
                .technologiesUsed(skills)
                .projectStartDate(request.getProjectStartDate())
                .projectEndDate(request.isCurrentlyWorking() ? null : request.getProjectEndDate())
                .currentlyWorking(request.isCurrentlyWorking())
                .projectImageUrl(request.getProjectImageUrl())
                .profile(profile)
                .build();

        return mapToResponse(projectRepository.save(project));
    }

    // ---------------- GET PROJECT BY ID ----------------
    public ProjectResponse getProjectById(int id) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
        return mapToResponse(project);
    }

    // ---------------- UPDATE PROJECT ----------------
    public ProjectResponse updateProjectById(int id, ProjectRequest request) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));

        Project duplicate = projectRepository.findByProjectNameAndProfileId(request.getProjectName(), request.getProfileId());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Another project with same name exists");
        }

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        project.setProjectName(request.getProjectName());
        project.setProjectDescription(request.getProjectDescription());
        project.setProjectLink(request.getProjectLink());
        project.setTechnologiesUsed(skillRepository.findAllById(request.getTechnologiesUsed()));
        project.setProjectStartDate(request.getProjectStartDate());
        project.setProjectEndDate(request.isCurrentlyWorking() ? null : request.getProjectEndDate());
        project.setCurrentlyWorking(request.isCurrentlyWorking());
        project.setProjectImageUrl(request.getProjectImageUrl());
        project.setProfile(profile);

        return mapToResponse(projectRepository.save(project));
    }

    // ---------------- DELETE PROJECT ----------------
    public String deleteProjectById(int id) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
        projectRepository.delete(project);
        return "Project deleted successfully";
    }

    // ---------------- GET PROJECTS BY PROFILE ( With Pagination ) ----------------
    public Page<ProjectResponse> getProjectByProfileId(Integer profileId, Pageable pageable, String search) {
        return projectRepository.findByProfileIdWithSearch(profileId, search, pageable)
                .map(this::mapToResponse);
    }

    // ---------------- GET PROJECTS BY PROFILE ----------------
    public List<ProjectResponse> getProjectByProfileId(Integer profileId) {
        List<Project> projects = projectRepository.findByProfileId(profileId);
        return projects.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ---------------- MAP ENTITY TO DTO ----------------
    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .projectLink(project.getProjectLink())
                .technologiesUsed(project.getTechnologiesUsed().stream()
                        .map(skill -> new SkillDropdown(skill.getId(), skill.getLogo().getName(), skill.getLogo().getUrl()))
                        .collect(Collectors.toList()))
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .currentlyWorking(project.isCurrentlyWorking())
                .projectImageUrl(project.getProjectImageUrl())
                .build();
    }
}
