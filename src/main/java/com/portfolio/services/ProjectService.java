package com.portfolio.services;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Project;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ProjectRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    public ProjectService(ProjectRepository projectRepository, SkillRepository skillRepository) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
    }

    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(ProjectRequest request) {
        if (projectRepository.findByProjectName(request.getProjectName()) != null) {
            return ApiResponse.failureResponse(null, "Project already exists with this name");
        }

        // fetch skills from IDs
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
                .build();

        Project saved = projectRepository.save(project);
        return ApiResponse.successResponse(mapToResponse(saved), "Project created successfully");
    }

    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(int id) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
        return ApiResponse.successResponse(mapToResponse(project), "Project found successfully");
    }

    public ResponseEntity<ResponseModel<ProjectResponse>> updateProjectById(int id, ProjectRequest request) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));

        // fetch updated skills from IDs
        List<Skill> skills = skillRepository.findAllById(request.getTechnologiesUsed());

        project.setProjectName(request.getProjectName());
        project.setProjectDescription(request.getProjectDescription());
        project.setProjectLink(request.getProjectLink());
        project.setTechnologiesUsed(skills);
        project.setProjectStartDate(request.getProjectStartDate());
        project.setProjectEndDate(request.isCurrentlyWorking() ? null : request.getProjectEndDate());
        project.setCurrentlyWorking(request.isCurrentlyWorking());
        project.setProjectImageUrl(request.getProjectImageUrl());

        Project updated = projectRepository.save(project);
        return ApiResponse.successResponse(mapToResponse(updated), "Project updated successfully");
    }

    public ResponseEntity<ResponseModel<List<ProjectResponse>>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectResponse> responses = projects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ApiResponse.successResponse(responses, "Projects fetched successfully");
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .projectLink(project.getProjectLink())
                .technologiesUsed(
                        project.getTechnologiesUsed().stream()
                                .map(skill -> new SkillDropdown(
                                        skill.getId(),
                                        skill.getLogo().getName(),
                                        skill.getLogo().getUrl()
                                ))
                                .collect(Collectors.toList())
                )
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .currentlyWorking(project.isCurrentlyWorking())
                .projectImageUrl(project.getProjectImageUrl())
                .build();
    }
}
