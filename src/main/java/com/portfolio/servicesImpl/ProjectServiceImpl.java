package com.portfolio.servicesImpl;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.entities.Project;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.WorkStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.ProjectRepository;
import com.portfolio.repositories.SkillRepository;
import com.portfolio.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ProjectResponse create(ProjectRequest req) throws GenericException {

        if (!profileRepository.existsById(req.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found");
        }
        if (projectRepository.existsByProjectNameAndProfileId(req.getProjectName(), req.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Project with same name already exists");
        }
        Project project = Project.builder()
                .profileId(req.getProfileId())
                .projectName(req.getProjectName())
                .projectDescription(req.getProjectDescription())
                .projectLink(req.getProjectLink())
                .projectStartDate(req.getProjectStartDate())
                .projectEndDate(req.getWorkStatus() == WorkStatusEnum.CURRENT ? null : req.getProjectEndDate())
                .workStatus(req.getWorkStatus())
                .projectImageUrl(req.getProjectImageUrl())
                .skillIds(req.getSkillIds())
                .build();
        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse update(String id, ProjectRequest req) throws GenericException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
        project.setProjectName(req.getProjectName());
        project.setProjectDescription(req.getProjectDescription());
        project.setProjectLink(req.getProjectLink());
        project.setProjectStartDate(req.getProjectStartDate());
        project.setProjectEndDate(req.getWorkStatus() == WorkStatusEnum.CURRENT ? null : req.getProjectEndDate());
        project.setWorkStatus(req.getWorkStatus());
        project.setProjectImageUrl(req.getProjectImageUrl());
        project.setSkillIds(req.getSkillIds());
        return mapToResponse(projectRepository.save(project));
    }

    // ---------------- GET BY ID ----------------
    @Override
    public ProjectResponse getById(String id) throws GenericException {
        return projectRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
    }

    @Override
    public String delete(String id) throws GenericException {
        if (!projectRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found");
        }
        projectRepository.deleteById(id);
        return "Project deleted successfully";
    }

    @Override
    public Page<ProjectResponse> getByProfile(String profileId, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasProfileId = profileId != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<Project> projects;
        if( hasSearch && hasProfileId){
            projects = projectRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            projects = projectRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            projects = projectRepository.findByProfileId(profileId, sortedPageable);
        }else{
            projects = projectRepository.findAll(sortedPageable);
        }
        return projects.map(this::mapToResponse);
    }

    @Override
    public List<ProjectResponse> getByProfile(String profileId) {
        return projectRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProjectResponse mapToResponse(Project project) {
        List<Skill> skills = project.getSkillIds() == null ? List.of() : skillRepository.findAllById(project.getSkillIds());
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .projectLink(project.getProjectLink())
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .workStatus(project.getWorkStatus())
                .projectImageUrl(project.getProjectImageUrl())
                .skills(
                        skills.stream()
                                .map(skill ->
                                        new SkillDropdown(
                                                skill.getId(),
                                                skill.getLogo().getName(),
                                                skill.getLogo().getUrl()
                                        ))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
