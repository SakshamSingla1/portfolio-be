package com.portfolio.repositories;

import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends MongoRepository<Education, String> {

    boolean existsByDegreeAndProfileId(DegreeEnum degree, String profileId);

    List<Education> findByProfileId(String profileId);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "institution": { "$regex": ?1, "$options": "i" } },
            { "fieldOfStudy": { "$regex": ?1, "$options": "i" } },
            { "location": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Education> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "institution": { "$regex": ?0, "$options": "i" } },
        { "fieldOfStudy": { "$regex": ?0, "$options": "i" } },
        { "location": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Education> findBySearch(String search, Pageable pageable);

    Page<Education> findAll(Pageable pageable);

    Page<Education> findByProfileId(String profileId, Pageable pageable);
}