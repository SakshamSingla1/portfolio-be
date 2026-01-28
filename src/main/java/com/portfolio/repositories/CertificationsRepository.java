package com.portfolio.repositories;

import com.portfolio.entities.Certifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CertificationsRepository extends MongoRepository<Certifications, String> {

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
    Page<Certifications> findByProfileIdWithSearch(
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
    Page<Certifications> findBySearch(String search, Pageable pageable);

    Page<Certifications> findAll(Pageable pageable);

    Page<Certifications> findByProfileId(String profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(String profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(String profileId, String order, String id);
}
