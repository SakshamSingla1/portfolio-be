package com.portfolio.repositories;

import com.portfolio.entities.ProjectImages;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectImageRepository extends MongoRepository<ProjectImages, String> {
    List<ProjectImages> findByProjectId(String projectId);
    void deleteByProjectId(String projectId);
}

