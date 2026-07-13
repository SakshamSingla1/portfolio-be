package com.portfolio.repositories;

import com.portfolio.entities.TestimonialRequestLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestimonialRequestLinkRepository extends JpaRepository<TestimonialRequestLink, Long> {

    List<TestimonialRequestLink> findByProfileIdOrderByCreatedAtDesc(Long profileId);

    Optional<TestimonialRequestLink> findByToken(String token);

    void deleteByProfileIdAndId(Long profileId, Long id);
}
