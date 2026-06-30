package com.portfolio.dao.landing;

import com.portfolio.dtos.LandingPage.LandingFaqResponse;
import com.portfolio.entities.LandingFaq;
import com.portfolio.repositories.LandingFaqRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingFaqDao {

    private final LandingFaqRepository landingFaqRepository;

    public LandingFaqDao(LandingFaqRepository landingFaqRepository) {
        this.landingFaqRepository = landingFaqRepository;
    }

    public LandingFaq save(LandingFaq faq) {
        return landingFaqRepository.save(faq);
    }

    public Optional<LandingFaq> findById(Long id) {
        return landingFaqRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingFaqRepository.deleteById(id);
    }

    public List<LandingFaq> findByIsActiveTrueOrderBySortOrderAsc() {
        return landingFaqRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<LandingFaqResponse> findActiveAsDTOs() {
        return landingFaqRepository.findActiveAsDTOs();
    }

    public Optional<LandingFaqResponse> findDTOById(Long id) {
        return landingFaqRepository.findDTOById(id);
    }

    public Page<LandingFaqResponse> findByCriteria(String search, Boolean isActive, Pageable pageable) {
        return landingFaqRepository.findByCriteria(search, isActive, pageable);
    }

    public List<LandingFaq> findAll() {
        return landingFaqRepository.findAll();
    }

    public void delete(LandingFaq faq) {
        landingFaqRepository.delete(faq);
    }
}
