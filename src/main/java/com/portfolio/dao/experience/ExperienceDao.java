package com.portfolio.dao.experience;

import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.entities.Experience;
import com.portfolio.repositories.ExperienceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ExperienceDao {

    private final ExperienceRepository experienceRepository;

    public ExperienceDao(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    public Experience save(Experience experience) {
        return experienceRepository.save(experience);
    }

    public Optional<Experience> findById(Long id) {
        return experienceRepository.findById(id);
    }

    public void deleteById(Long id) {
        experienceRepository.deleteById(id);
    }

    public boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
            Long profileId, String companyName, String jobTitle, LocalDate startDate) {
        return experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
                profileId, companyName, jobTitle, startDate);
    }

    public boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
            Long profileId, String companyName, String jobTitle, LocalDate startDate, Long id) {
        return experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
                profileId, companyName, jobTitle, startDate, id);
    }

    public List<Experience> findByProfileId(Long profileId) {
        return experienceRepository.findByProfileId(profileId);
    }

    public long countByProfileId(Long profileId) {
        return experienceRepository.countByProfileId(profileId);
    }

    public Optional<Experience> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return experienceRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public Page<ExperienceResponse> findByCriteria(Long profileId, String search, Pageable pageable){
        return experienceRepository.findByCriteria(profileId,search,pageable);
    }
}
