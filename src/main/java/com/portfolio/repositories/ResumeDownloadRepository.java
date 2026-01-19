package com.portfolio.repositories;

import com.portfolio.entities.ResumeDownload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeDownloadRepository extends MongoRepository<ResumeDownload, String> {
    long countByProfileId(String profileId);
}
