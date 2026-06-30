package com.portfolio.dao.resume;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.Resume;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ResumeDao {

    private final ResumeRepository resumeRepository;

    public ResumeDao(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Resume save(Resume resume) {
        return resumeRepository.save(resume);
    }

    public Optional<Resume> findById(Long id) {
        return resumeRepository.findById(id);
    }

    public void deleteById(Long id) {
        resumeRepository.deleteById(id);
    }

    public List<Resume> findByProfileId(Long profileId) {
        return resumeRepository.findByProfileId(profileId);
    }

    public Optional<Resume> findByProfileIdAndStatus(Long profileId, StatusEnum status) {
        return resumeRepository.findByProfileIdAndStatus(profileId, status);
    }

    public Page<ResumeUploadResponseDTO> findByCriteria(Long profileId, StatusEnum status, String search, Pageable pageable) {
        return resumeRepository.findByCriteria(profileId, status, search, pageable);
    }

    public Optional<ResumeUploadResponseDTO> findDTOById(Long id) {
        return resumeRepository.findDTOById(id);
    }
}
