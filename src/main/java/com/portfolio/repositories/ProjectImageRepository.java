package com.portfolio.repositories;

import com.portfolio.entities.ProjectImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImages, Long> {

    List<ProjectImages> findByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
