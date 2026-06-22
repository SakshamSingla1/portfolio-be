package com.portfolio.repositories;

import com.portfolio.entities.LandingHowToUseStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandingHowToUseStepRepository extends JpaRepository<LandingHowToUseStep, Long> {

    List<LandingHowToUseStep> findByIsActiveTrueOrderBySortOrderAsc();
}
