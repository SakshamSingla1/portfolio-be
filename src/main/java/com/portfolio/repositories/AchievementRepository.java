package com.portfolio.repositories;

import com.portfolio.entities.Achievements;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends MongoRepository<Achievements, String> {

    @Query("""
    {
      $and: [
        {
          $or: [
            { "title":  { "$regex": ?1, "$options": "i" } },
            { "issuer": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Achievements> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "title":  { "$regex": ?0, "$options": "i" } },
        { "issuer": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Achievements> findBySearch(String search, Pageable pageable);

    Page<Achievements> findAll(Pageable pageable);

    Page<Achievements> findByProfileId(String profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(String profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(String profileId, String order, String id);

    List<Achievements> findByProfileIdAndStatusOrderByOrderAsc(String profileId, StatusEnum status);
}
