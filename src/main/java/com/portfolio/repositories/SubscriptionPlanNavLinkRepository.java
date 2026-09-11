package com.portfolio.repositories;

import com.portfolio.entities.SubscriptionPlanNavLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubscriptionPlanNavLinkRepository extends JpaRepository<SubscriptionPlanNavLink, Long> {

    List<SubscriptionPlanNavLink> findByPlanId(Long planId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM SubscriptionPlanNavLink s WHERE s.planId = :planId")
    void deleteByPlanId(@Param("planId") Long planId);

    // Nav links with zero rows across every plan mapping stay universally accessible —
    // e.g. platform-admin-only screens that are never assigned to any plan.
    @Query("SELECT n.id FROM NavLink n WHERE n.id NOT IN (SELECT DISTINCT s.navLinkId FROM SubscriptionPlanNavLink s)")
    List<Long> findUngatedNavLinkIds();
}
