package com.portfolio.dao.landing;

import com.portfolio.dtos.LandingPage.LandingFeatureResponse;
import com.portfolio.entities.LandingFeature;
import com.portfolio.repositories.LandingFeatureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingFeatureDao {

    private final LandingFeatureRepository landingFeatureRepository;

    public LandingFeatureDao(LandingFeatureRepository landingFeatureRepository) {
        this.landingFeatureRepository = landingFeatureRepository;
    }

    public LandingFeature save(LandingFeature feature) {
        return landingFeatureRepository.save(feature);
    }

    public Optional<LandingFeature> findById(Long id) {
        return landingFeatureRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingFeatureRepository.deleteById(id);
    }

    public List<LandingFeature> findByIsActiveTrueOrderBySortOrderAsc() {
        return landingFeatureRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<LandingFeatureResponse> findActiveAsDTOs() {
        return landingFeatureRepository.findActiveAsDTOs();
    }

    public Optional<LandingFeatureResponse> findDTOById(Long id) {
        return landingFeatureRepository.findDTOById(id);
    }

    public Page<LandingFeatureResponse> findByCriteria(String search, Boolean isActive, Pageable pageable) {
        return landingFeatureRepository.findByCriteria(search, isActive, pageable);
    }

    public List<LandingFeature> findAll() {
        return landingFeatureRepository.findAll();
    }

    public void delete(LandingFeature feature) {
        landingFeatureRepository.delete(feature);
    }
}
