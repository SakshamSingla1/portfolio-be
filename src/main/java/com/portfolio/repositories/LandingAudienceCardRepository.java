package com.portfolio.repositories;

import com.portfolio.entities.LandingAudienceCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandingAudienceCardRepository extends JpaRepository<LandingAudienceCard, Long> {

    List<LandingAudienceCard> findByIsActiveTrueOrderBySortOrderAsc();
}
