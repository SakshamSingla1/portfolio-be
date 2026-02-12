package com.portfolio.repositories;

import com.portfolio.entities.ContactUs;
import com.portfolio.enums.ContactUsStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactUsRepository extends MongoRepository<ContactUs, String> {

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name":  { "$regex": ?1, "$options": "i" } },
            { "email": { "$regex": ?1, "$options": "i" } }
          ]
        },
        { "profileId": ?0 }
      ]
    }
    """)
    Page<ContactUs> findByProfileIdWithSearch(
            String profileId,
            String search,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "name":  { "$regex": ?0, "$options": "i" } },
        { "email": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<ContactUs> findBySearch(String search, Pageable pageable);

    Page<ContactUs> findAll(Pageable pageable);

    Page<ContactUs> findByProfileId(String profileId, Pageable pageable);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateStatusById(String id, ContactUsStatusEnum status);
}
