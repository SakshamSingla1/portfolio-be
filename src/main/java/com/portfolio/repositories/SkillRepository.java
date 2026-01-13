package com.portfolio.repositories;

import com.portfolio.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends MongoRepository<Skill, String> {

    @Query("""
    {
      $and: [
        {
          $or: [
            { "logo.name": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Skill> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "logo.name": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Skill> findBySearch(String search, Pageable pageable);

    Page<Skill> findAll(Pageable pageable);

    Page<Skill> findByProfileId(String profileId, Pageable pageable);

    List<Skill> findByProfileId(String profileId);

    boolean existsByLogoIdAndProfileId(String logoId, String profileId);
}
