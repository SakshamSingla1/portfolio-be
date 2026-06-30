package com.portfolio.dao.landing;

import com.portfolio.dtos.LandingPage.LandingTestimonialResponse;
import com.portfolio.entities.LandingTestimonial;
import com.portfolio.repositories.LandingTestimonialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class LandingTestimonialDao {

    private final LandingTestimonialRepository landingTestimonialRepository;

    public LandingTestimonialDao(LandingTestimonialRepository landingTestimonialRepository) {
        this.landingTestimonialRepository = landingTestimonialRepository;
    }

    public LandingTestimonial save(LandingTestimonial testimonial) {
        return landingTestimonialRepository.save(testimonial);
    }

    public Optional<LandingTestimonial> findById(Long id) {
        return landingTestimonialRepository.findById(id);
    }

    public void deleteById(Long id) {
        landingTestimonialRepository.deleteById(id);
    }

    public List<LandingTestimonial> findByIsActiveTrueOrderBySortOrderAsc() {
        return landingTestimonialRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<LandingTestimonialResponse> findActiveAsDTOs() {
        return landingTestimonialRepository.findActiveAsDTOs();
    }

    public Optional<LandingTestimonialResponse> findDTOById(Long id) {
        return landingTestimonialRepository.findDTOById(id);
    }

    public Page<LandingTestimonialResponse> findByCriteria(String search, Boolean isActive, Pageable pageable) {
        return landingTestimonialRepository.findByCriteria(search, isActive, pageable);
    }

    public List<LandingTestimonial> findAll() {
        return landingTestimonialRepository.findAll();
    }

    public void delete(LandingTestimonial testimonial) {
        landingTestimonialRepository.delete(testimonial);
    }
}
