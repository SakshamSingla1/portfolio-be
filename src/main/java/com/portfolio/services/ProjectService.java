package com.portfolio.services;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.entities.Project;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(ProjectRequest projectRequest) throws GenericException {
        Project existingProject =  projectRepository.findByProjectName(projectRequest.getProjectName());
        if (existingProject != null) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROJECT,"Project with same name already exists");
        }
        Project project = Project.builder()
                .projectName(projectRequest.getProjectName())
                .projectDescription(projectRequest.getProjectDescription())
                .projectDuration(projectRequest.getProjectDuration())
                .projectLink(projectRequest.getProjectLink())
                .technologiesUsed(projectRequest.getTechnologiesUsed())
                .build();

        Project savedProject = projectRepository.save(project);

        ProjectResponse response = ProjectResponse.builder()
                .id(savedProject.getId())
                .projectName(savedProject.getProjectName())
                .projectDescription(savedProject.getProjectDescription())
                .projectLink(savedProject.getProjectLink())
                .projectDuration(savedProject.getProjectDuration())
                .technologiesUsed(savedProject.getTechnologiesUsed())
                .projectStartDate(savedProject.getProjectStartDate())
                .projectEndDate(savedProject.getProjectEndDate())
                .technologiesUsed(savedProject.getTechnologiesUsed())
                .build();

        return ApiResponse.successResponse(response,"Project created successfully");
    }

    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(int id) throws GenericException {
        Project existingProject = projectRepository.findById(id).get();
        if (existingProject == null) {
            throw new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND,"Project not found");
        }
        ProjectResponse response = ProjectResponse.builder()
                .id(existingProject.getId())
                .id(existingProject.getId())
                .projectName(existingProject.getProjectName())
                .projectDescription(existingProject.getProjectDescription())
                .projectLink(existingProject.getProjectLink())
                .projectDuration(existingProject.getProjectDuration())
                .technologiesUsed(existingProject.getTechnologiesUsed())
                .projectStartDate(existingProject.getProjectStartDate())
                .projectEndDate(existingProject.getProjectEndDate())
                .technologiesUsed(existingProject.getTechnologiesUsed())
                .build();
        return ApiResponse.successResponse(response,"Project found successfully");
    }

    public ResponseEntity<ResponseModel<ProjectResponse>> updateProjectById(int id, ProjectRequest projectRequest) throws GenericException {
        Project existingProject = projectRepository.findById(id).get();
        if (existingProject == null) {
            throw new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND,"Project not found");
        }
        existingProject.setProjectName(projectRequest.getProjectName());
        existingProject.setProjectDescription(projectRequest.getProjectDescription());
        existingProject.setProjectDuration(projectRequest.getProjectDuration());
        existingProject.setProjectLink(projectRequest.getProjectLink());
        existingProject.setTechnologiesUsed(projectRequest.getTechnologiesUsed());

        Project updatedProject = projectRepository.save(existingProject);
        ProjectResponse response = ProjectResponse.builder()
                .id(updatedProject.getId())
                .id(updatedProject.getId())
                .projectName(updatedProject.getProjectName())
                .projectDescription(updatedProject.getProjectDescription())
                .projectLink(updatedProject.getProjectLink())
                .projectDuration(updatedProject.getProjectDuration())
                .technologiesUsed(updatedProject.getTechnologiesUsed())
                .projectStartDate(updatedProject.getProjectStartDate())
                .projectEndDate(updatedProject.getProjectEndDate())
                .technologiesUsed(updatedProject.getTechnologiesUsed())
                .build();

        return ApiResponse.successResponse(response,"Project updated successfully");
    }

    public ResponseEntity<ResponseModel<List<ProjectResponse>>> getAllProjects() throws GenericException {
        List<Project> projects = projectRepository.findAll();
        List<ProjectResponse> response = projects.stream()
                .map(project -> ProjectResponse.builder()
                        .id(project.getId())
                        .projectName(project.getProjectName())
                        .projectDescription(project.getProjectDescription())
                        .projectLink(project.getProjectLink())
                        .projectDuration(project.getProjectDuration())
                        .projectStartDate(project.getProjectStartDate())
                        .projectEndDate(project.getProjectEndDate())
                        .technologiesUsed(project.getTechnologiesUsed())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Project found successfully");
    }

}
