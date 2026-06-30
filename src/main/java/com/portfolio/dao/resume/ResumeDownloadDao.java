package com.portfolio.dao.resume;

import com.portfolio.entities.ResumeDownload;
import com.portfolio.repositories.ResumeDownloadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ResumeDownloadDao {

    private final ResumeDownloadRepository resumeDownloadRepository;

    public ResumeDownloadDao(ResumeDownloadRepository resumeDownloadRepository) {
        this.resumeDownloadRepository = resumeDownloadRepository;
    }

    public ResumeDownload save(ResumeDownload resumeDownload) {
        return resumeDownloadRepository.save(resumeDownload);
    }

    public long countByProfileId(Long profileId) {
        return resumeDownloadRepository.countByProfileId(profileId);
    }
}
