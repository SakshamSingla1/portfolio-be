package com.portfolio.repositories;

import com.portfolio.entities.Resume;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    List<Resume> findByProfileId(String profileId);

    Optional<Resume> findByProfileIdAndStatus(String profileId, StatusEnum status);

    Page<Resume> findAll(Pageable pageable);

    Page<Resume> findByStatus(StatusEnum status, Pageable pageable);

    Page<Resume> findByProfileId(String profileId, Pageable pageable);

    Page<Resume> findByStatusAndProfileId(StatusEnum status, String profileId, Pageable pageable);

    @Query("""
            {
              $or: [
                { "fileName": { $regex: ?0, $options: "i" } },
              ]
            }
            """)
    Page<Resume> search(String search, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "fileName": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "status": ?1 }
              ]
            }
            """)
    Page<Resume> searchByStatus(String search, StatusEnum status, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "fileName": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "profileId": ?1 }
              ]
            }
            """)
    Page<Resume> searchByProfileId(String search, String profileId, Pageable pageable);

    @Query("""
            {
              $and: [
                {
                  $or: [
                    { "fileName": { $regex: ?0, $options: "i" } },
                  ]
                },
                { "status": ?1 },
                { "profileId": ?2 }
              ]
            }
            """)
    Page<Resume> searchByProfileIdAndStatus(
            String search,
            StatusEnum status,
            String profileId,
            Pageable pageable
    );
}
