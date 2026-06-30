package com.portfolio.dao.landing;

import com.portfolio.entities.LandingPageConfig;
import com.portfolio.repositories.LandingPageConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingPageConfigDao {

    private final LandingPageConfigRepository landingPageConfigRepository;

    public LandingPageConfigDao(LandingPageConfigRepository landingPageConfigRepository) {
        this.landingPageConfigRepository = landingPageConfigRepository;
    }

    public LandingPageConfig save(LandingPageConfig config) {
        return landingPageConfigRepository.save(config);
    }

    public Optional<LandingPageConfig> findById(Long id) {
        return landingPageConfigRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingPageConfigRepository.deleteById(id);
    }

    public List<LandingPageConfig> findAll() {
        return landingPageConfigRepository.findAll();
    }
}
