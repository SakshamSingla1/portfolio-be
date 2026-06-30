package com.portfolio.dao.landing;

import com.portfolio.dtos.LandingPage.LandingAudienceCardResponse;
import com.portfolio.entities.LandingAudienceCard;
import com.portfolio.repositories.LandingAudienceCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingAudienceCardDao {

    private final LandingAudienceCardRepository landingAudienceCardRepository;

    public LandingAudienceCardDao(LandingAudienceCardRepository landingAudienceCardRepository) {
        this.landingAudienceCardRepository = landingAudienceCardRepository;
    }

    public LandingAudienceCard save(LandingAudienceCard card) {
        return landingAudienceCardRepository.save(card);
    }

    public Optional<LandingAudienceCard> findById(Long id) {
        return landingAudienceCardRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingAudienceCardRepository.deleteById(id);
    }

    public List<LandingAudienceCard> findByIsActiveTrueOrderBySortOrderAsc() {
        return landingAudienceCardRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<LandingAudienceCardResponse> findActiveAsDTOs() {
        return landingAudienceCardRepository.findActiveAsDTOs();
    }

    public Optional<LandingAudienceCardResponse> findDTOById(Long id) {
        return landingAudienceCardRepository.findDTOById(id);
    }

    public Page<LandingAudienceCardResponse> findByCriteria(String search, Boolean isActive, Pageable pageable) {
        return landingAudienceCardRepository.findByCriteria(search, isActive, pageable);
    }

    public List<LandingAudienceCard> findAll() {
        return landingAudienceCardRepository.findAll();
    }

    public void delete(LandingAudienceCard card) {
        landingAudienceCardRepository.delete(card);
    }
}
