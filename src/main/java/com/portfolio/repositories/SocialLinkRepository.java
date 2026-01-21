package com.portfolio.repositories;

import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLinkRepository extends MongoRepository<SocialLinks,String> {
    List<SocialLinks> getByProfileId(String profileId);

    Optional<SocialLinks> findByPlatform(PlatformEnum platform);

    Optional<SocialLinks> findByProfileIdAndPlatformAndStatus(String profileId,PlatformEnum platform,StatusEnum status);

    boolean existsByProfileIdAndPlatformAndStatusNot(String profileId,PlatformEnum platform,StatusEnum status);

    Page<SocialLinks> findAll(Pageable pageable);

    Page<SocialLinks> findByStatus(StatusEnum status, Pageable pageable);

    Page<SocialLinks> findByProfileId(String profileId, Pageable pageable);

    Page<SocialLinks> findByStatusAndProfileId(StatusEnum status, String profileId, Pageable pageable);

    @Query("""
            {
              $or: [
                { "platform": { $regex: ?0, $options: "i" } },
              ]
            }
            """)
    Page<SocialLinks> search(String search, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "platform": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "status": ?1 }
              ]
            }
            """)
    Page<SocialLinks> searchByStatus(String search, StatusEnum status, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "platform": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "profileId": ?1 }
              ]
            }
            """)
    Page<SocialLinks> searchByProfileId(String search, String profileId, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "platform": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "status": ?1 },
                { "profileId": ?2 }
              ]
            }
            """)
    Page<SocialLinks> searchByProfileIdAndStatus(
            String search,
            StatusEnum status,
            String profileId,
            Pageable pageable
    );
}
