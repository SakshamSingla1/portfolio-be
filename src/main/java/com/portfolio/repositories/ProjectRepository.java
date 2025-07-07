package com.portfolio.repositories;

import com.portfolio.entities.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {
    Project findByProjectName(String name);
    List<Project> findAll();
}
