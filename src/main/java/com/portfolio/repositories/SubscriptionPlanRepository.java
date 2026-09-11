package com.portfolio.repositories;

import com.portfolio.entities.SubscriptionPlan;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByName(String name);

    Optional<SubscriptionPlan> findByCode(String code);

    Optional<SubscriptionPlan> findByIsDefaultTrue();

    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("""
            SELECT p FROM SubscriptionPlan p
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(p.code) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<SubscriptionPlan> findByCriteria(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE SubscriptionPlan p SET p.isDefault = false WHERE p.id <> :id")
    void clearDefaultExcept(@Param("id") Long id);
}
