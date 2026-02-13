package com.portfolio.repositories;

import com.portfolio.entities.Testimonial;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestimonialRepository extends MongoRepository<Testimonial, String> {
    @Query("""
    {
      $and: [
        {
          $or: [
            { "name":  { "$regex": ?1, "$options": "i" } },
            { "company": { "$regex": ?1, "$options": "i" } },
            { "role": { "$regex": ?1, "$options": "i" } },
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<Testimonial> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "name":  { "$regex": ?1, "$options": "i" } },
        { "company": { "$regex": ?1, "$options": "i" } },
        { "role": { "$regex": ?1, "$options": "i" } },
      ]
    }
    """)
    Page<Testimonial> findBySearch(String search, Pageable pageable);

    Page<Testimonial> findAll(Pageable pageable);

    Page<Testimonial> findByProfileId(String profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(String profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(String profileId, String order, String id);

    List<Testimonial> findByProfileIdAndStatusOrderByOrderAsc(String profileId, StatusEnum statusEnum);

    long countByProfileId(String profileId);

    Optional<Testimonial> findTop1ByProfileIdOrderByUpdatedAtDesc(String profileId);
}
