package com.portfolio.dao.education;

import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.repositories.EducationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class EducationDao {

    private final EducationRepository educationRepository;

    public EducationDao(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    public Education save(Education education) {
        return educationRepository.save(education);
    }

    public Optional<Education> findById(Long id) {
        return educationRepository.findById(id);
    }

    public void deleteById(Long id) {
        educationRepository.deleteById(id);
    }

    public boolean existsByDegreeAndProfileId(DegreeEnum degree, Long profileId) {
        return educationRepository.existsByDegreeAndProfileId(degree, profileId);
    }

    public List<Education> findByProfileId(Long profileId) {
        return educationRepository.findByProfileId(profileId);
    }

    public long countByProfileId(Long profileId) {
        return educationRepository.countByProfileId(profileId);
    }

    public Optional<Education> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return educationRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public void delete(Education education) {
        educationRepository.delete(education);
    }

    public Optional<EducationResponse> findDTOByIdAndProfileId(Long id, Long profileId) {
        return educationRepository.findDTOByIdAndProfileId(id, profileId);
    }

    public Page<EducationResponse> findByCriteria(Long profileId, String search, Pageable pageable){
        return educationRepository.findByCriteria(profileId,search,pageable);
    }
}
