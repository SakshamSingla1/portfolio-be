package com.portfolio.dao.landing;

import com.portfolio.entities.LandingHowToUseStep;
import com.portfolio.repositories.LandingHowToUseStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingHowToUseStepDao {

    private final LandingHowToUseStepRepository landingHowToUseStepRepository;

    public LandingHowToUseStepDao(LandingHowToUseStepRepository landingHowToUseStepRepository) {
        this.landingHowToUseStepRepository = landingHowToUseStepRepository;
    }

    public LandingHowToUseStep save(LandingHowToUseStep step) {
        return landingHowToUseStepRepository.save(step);
    }

    public Optional<LandingHowToUseStep> findById(Long id) {
        return landingHowToUseStepRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingHowToUseStepRepository.deleteById(id);
    }

    public List<LandingHowToUseStep> findByIsActiveTrueOrderBySortOrderAsc() {
        return landingHowToUseStepRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<LandingHowToUseStep> findAll() {
        return landingHowToUseStepRepository.findAll();
    }

    public void delete(LandingHowToUseStep step) {
        landingHowToUseStepRepository.delete(step);
    }
}
