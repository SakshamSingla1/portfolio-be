package com.portfolio.repositories;

import com.portfolio.entities.Resume;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    List<Resume> findByProfileIdAndStatusNotOrderByUploadedAtDesc(String profileId, StatusEnum status);
    Optional<Resume> findByProfileIdAndStatus(String profileId, StatusEnum status);
    boolean existsByProfileIdAndStatus(String profileId, StatusEnum status);
}
