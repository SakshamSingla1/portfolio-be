package com.portfolio.repositories;

import com.portfolio.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {

    boolean existsByProjectNameAndProfileId(String projectName, String profileId);

    List<Project> findByProfileId(String profileId);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "projectName": { "$regex": ?1, "$options": "i" } },
            { "projectDescription": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Project> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "projectName": { "$regex": ?0, "$options": "i" } },
        { "projectDescription": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Project> findBySearch(String search, Pageable pageable);

    Page<Project> findAll(Pageable pageable);

    Page<Project> findByProfileId(String profileId, Pageable pageable);
}
