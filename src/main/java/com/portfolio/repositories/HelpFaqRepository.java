package com.portfolio.repositories;

import com.portfolio.entities.HelpFaq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpFaqRepository extends JpaRepository<HelpFaq, Long> {

    List<HelpFaq> findByIsActiveTrueOrderBySortOrderAsc();
}
