package com.portfolio.repositories;

import com.portfolio.entities.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends MongoRepository<Experience, String> {

    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
            String profileId,
            String companyName,
            String jobTitle,
            LocalDate startDate
    );

    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
            String profileId,
            String companyName,
            String jobTitle,
            LocalDate startDate,
            String id
    );

    @Query("""
    {
      $and: [
        {
          $or: [
            { "companyName": { "$regex": ?1, "$options": "i" } },
            { "jobTitle": { "$regex": ?1, "$options": "i" } },
            { "location": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Experience> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "companyName": { "$regex": ?0, "$options": "i" } },
        { "jobTitle": { "$regex": ?0, "$options": "i" } },
        { "location": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Experience> findBySearch(String search, Pageable pageable);

    Page<Experience> findAll(Pageable pageable);

    Page<Experience> findByProfileId(String profileId, Pageable pageable);

    List<Experience> findByProfileId(String profileId);

    long countByProfileId(String profileId);

    Optional<Experience> findTop1ByProfileIdOrderByUpdatedAtDesc(String profileId);
}
