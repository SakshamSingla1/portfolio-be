package com.portfolio.dao.testimonial_request;

import com.portfolio.entities.TestimonialRequestLink;
import com.portfolio.repositories.TestimonialRequestLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TestimonialRequestLinkDao {

    private final TestimonialRequestLinkRepository testimonialRequestLinkRepository;

    public TestimonialRequestLinkDao(TestimonialRequestLinkRepository testimonialRequestLinkRepository) {
        this.testimonialRequestLinkRepository = testimonialRequestLinkRepository;
    }

    public TestimonialRequestLink save(TestimonialRequestLink link) {
        return testimonialRequestLinkRepository.save(link);
    }

    public Optional<TestimonialRequestLink> findById(Long id) {
        return testimonialRequestLinkRepository.findById(id);
    }

    public void deleteById(Long id) {
        testimonialRequestLinkRepository.deleteById(id);
    }

    public List<TestimonialRequestLink> findByProfileId(Long profileId) {
        return testimonialRequestLinkRepository.findByProfileIdOrderByCreatedAtDesc(profileId);
    }

    public Optional<TestimonialRequestLink> findByToken(String token) {
        return testimonialRequestLinkRepository.findByToken(token);
    }
}
