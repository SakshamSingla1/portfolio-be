package com.portfolio.dao.services;

import com.portfolio.entities.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOfferingDao extends JpaRepository<ServiceOffering, Long> {
    Page<ServiceOffering> findByProfileIdAndTitleContainingIgnoreCase(Long profileId, String title, Pageable pageable);
    Page<ServiceOffering> findByProfileId(Long profileId, Pageable pageable);
    List<ServiceOffering> findByProfileIdAndIsActiveTrueOrderBySortOrderAsc(Long profileId);
}
