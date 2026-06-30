package com.portfolio.dao.project;

import com.portfolio.entities.Project;
import com.portfolio.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ProjectDao {

    private final ProjectRepository projectRepository;

    public ProjectDao(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    public boolean existsByProjectNameAndProfileId(String projectName, Long profileId) {
        return projectRepository.existsByProjectNameAndProfileId(projectName, profileId);
    }

    public List<Project> findByProfileId(Long profileId) {
        return projectRepository.findByProfileId(profileId);
    }

    public Page<Project> findByCriteria(Long profileId, String search, Pageable pageable) {
        return projectRepository.findByCriteria(profileId, search, pageable);
    }

    public Page<Project> findByProfileId(Long profileId, Pageable pageable) {
        return projectRepository.findByProfileId(profileId, pageable);
    }

    public long countByProfileId(Long profileId) {
        return projectRepository.countByProfileId(profileId);
    }

    public Optional<Project> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return projectRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

}
